# Testing Strategy

이 문서는 테스트 종류, 모듈별 위치, 검증 기준을 정한다.

## 테스트 피라미드

```text
unit tests
→ integration tests
→ end-to-end tests
→ benchmark runs
```

원칙:

- 검색 알고리즘 정확도는 빠른 unit test로 고정한다.
- Object Storage와 RDB는 통합 테스트로 검증한다.
- 업로드부터 검색까지의 흐름은 E2E 테스트로 최소 시나리오를 둔다.
- 성능 수치는 테스트 assertion이 아니라 benchmark report로 관리한다.

## search-core

테스트 성격:

- Spring 없는 순수 JUnit 테스트
- 작은 고정 코퍼스 기반 ranking 검증

대상:

```text
Analyzer
Tokenizer
TokenFilter
InvertedIndex
PostingList
Bm25Scorer
TopKCollector
SearchEngine
```

필수 시나리오:

- 대소문자 정규화
- term frequency 계산
- document frequency 계산
- BM25 점수 계산
- Top-K 제한
- 동점 시 `documentId asc`
- 빈 query와 미존재 term

## search-storage

테스트 성격:

- MinIO/Testcontainers 또는 local Docker MinIO 기반 통합 테스트

대상:

```text
ObjectStorage.put
ObjectStorage.get
ObjectStorage.exists
ObjectStorage.delete
```

필수 시나리오:

- 원본 파일 저장/조회
- 추출 텍스트 저장/조회
- 존재하지 않는 key 처리
- 삭제 후 exists=false

## search-api

테스트 성격:

- Controller 테스트
- Application service 테스트
- 업로드-추출-색인-검색 E2E 테스트

필수 시나리오:

- txt/md 업로드 성공
- 지원하지 않는 파일 타입 거부
- 빈 파일 거부
- 업로드 성공 후 metadata row 생성
- 색인 성공 후 검색 결과에 문서 등장
- 색인 실패 시 `FAILED` 상태 기록

## search-benchmark

테스트 성격:

- 고정 seed 기반 benchmark 실행
- report 생성 검증

규칙:

- benchmark 결과는 환경에 따라 달라질 수 있으므로 일반 테스트에서 latency 수치를 assert하지 않는다.
- report schema와 필수 metric 존재 여부만 검증한다.

## 실행 명령

초기 예상 명령:

```powershell
.\gradlew.bat :search-core:test
.\gradlew.bat :search-storage:integrationTest
.\gradlew.bat :search-api:integrationTest
.\gradlew.bat :search-benchmark:run
```

현재 단일 모듈 단계에서는:

```powershell
.\gradlew.bat test
```
