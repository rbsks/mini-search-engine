# Operations Runbook

이 문서는 로컬 개발, AWS 테스트, 장애 복구, 색인 rebuild 절차를 정한다.

## Local Development

기본 구성:

```text
Spring Boot API
PostgreSQL
MinIO
local search index directory
```

예상 실행:

```powershell
docker compose -f docker/compose.local.yml up -d
$env:JAVA_HOME=$env:JAVA_HOME_21
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat test
```

현재 주의:

- 기본 `JAVA_HOME`이 JDK 11일 수 있으므로 Gradle 실행 전 JDK 21을 명시한다.
- IntelliJ IDEA 2024.3.7 기준 Kotlin 2.1.21, Spring Boot 3.4.13, Gradle 8.12.1 조합을 사용한다.

## Local MinIO

초기 bucket 후보:

```text
mini-search-documents
```

용도:

```text
documents/{documentId}/original/...
documents/{documentId}/extracted/text.txt
```

규칙:

- 로컬과 AWS의 storage API 사용 방식은 같아야 한다.
- profile/config만 바꿔 MinIO와 S3를 교체한다.

## AWS Test Environment

권장 구성:

```text
Application: EC2/ECS/EKS 중 선택
Object Storage: S3
Metadata DB: RDS PostgreSQL
Search index: EBS/EFS 또는 rebuild 가능한 local volume
```

초기 테스트에서는 운영 완성도보다 다음을 확인한다.

- S3 업로드/다운로드
- RDS metadata 저장
- search-core 색인 생성
- 재시작 후 색인 복구 또는 rebuild 가능성

## Index Rebuild

rebuild 기준:

```text
index directory 유실
segment format 변경
ranking/index 구조 변경
색인 corruption 의심
```

절차:

```text
1. 신규 index directory 생성
2. RDB에서 INDEXED 또는 재색인 대상 document 목록 조회
3. Object Storage에서 extracted text 조회
4. search-core로 재색인
5. manifest 교체
6. 검색 smoke test 실행
```

규칙:

- rebuild 중 기존 index를 바로 삭제하지 않는다.
- 새 index가 검증된 뒤 active index pointer를 교체한다.
- 실패 시 기존 index로 되돌릴 수 있어야 한다.

## Failure Handling

Object Storage 장애:

- 업로드 요청은 실패 처리한다.
- RDB에는 실패 상태 또는 보상 삭제 결과를 남긴다.

RDB 장애:

- metadata 저장 전이면 업로드 object를 보상 삭제한다.
- metadata 저장 후 상태 갱신 실패는 재처리 가능한 로그를 남긴다.

Search index 장애:

- source of truth가 아니므로 rebuild 대상으로 본다.
- API는 검색 불가 상태를 명확한 오류로 반환한다.

## Smoke Check

배포 또는 rebuild 후 최소 확인:

```text
문서 업로드 성공
문서 상태 조회 성공
검색 query 결과 반환
존재하지 않는 query가 빈 결과 반환
MinIO/S3 object 존재 확인
```
