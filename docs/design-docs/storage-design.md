# Storage Design

이 문서는 원본 파일, 추출 텍스트, 문서 메타데이터, 검색 색인의 저장 책임을 정한다.

## 저장 책임

```text
Object Storage
├─ original uploaded file
└─ extracted text

RDB
└─ document metadata and indexing status

search-core index directory
└─ derived search index files
```

원칙:

- RDB와 Object Storage가 source of truth다.
- search index files는 언제든 rebuild 가능한 파생 데이터다.
- 원본 파일과 추출 텍스트는 RDB에 직접 저장하지 않는다.
- 로컬은 MinIO, AWS는 S3를 사용한다.

## Object Storage Key 규칙

초기 key 형식:

```text
documents/{documentId}/original/{safeFileName}
documents/{documentId}/extracted/text.txt
```

규칙:

- key에는 사용자 입력 파일명을 그대로 쓰지 않는다.
- 파일명은 표시용 metadata로만 RDB에 저장한다.
- 같은 문서를 재처리할 수 있도록 `documentId` 기준 prefix를 사용한다.
- 파일 타입 확장보다 `content_type` metadata를 우선한다.

## RDB Metadata

초기 `documents` 테이블 후보:

```text
id
original_file_name
original_storage_key
extracted_text_storage_key
content_type
size
checksum
index_status
failure_reason
created_at
updated_at
```

초기 상태:

```text
UPLOADED
TEXT_EXTRACTED
INDEXED
FAILED
```

규칙:

- 목록 조회에서는 추출 텍스트 본문을 읽지 않는다.
- 실패 원인은 운영자가 원인을 알 수 있을 정도로만 저장하고 원문 전체를 저장하지 않는다.
- checksum은 중복 업로드 검출 또는 무결성 확인에 사용한다.

## Search Index Directory

초기 로컬 경로:

```text
data/index
```

향후 segment 도입 후 후보 구조:

```text
data/index
├─ manifest.json
├─ segments
│  ├─ segment_000001
│  └─ segment_000002
└─ tombstones
```

규칙:

- index directory 안에는 검색에 필요한 파생 데이터만 둔다.
- 원본 binary 파일은 index directory에 저장하지 않는다.
- index directory 삭제 시 RDB/Object Storage로 rebuild할 수 있어야 한다.

## 실패와 보상 처리

업로드 흐름:

```text
Object Storage original 저장
→ RDB metadata 저장
→ 텍스트 추출
→ Object Storage extracted text 저장
→ search-core 색인
→ RDB index_status 갱신
```

실패 기준:

- original 저장 실패: RDB row를 만들지 않는다.
- original 저장 성공 후 RDB 저장 실패: original object를 보상 삭제한다.
- 텍스트 추출 실패: `FAILED` 상태와 실패 원인을 기록한다.
- extracted text 저장 실패: `FAILED` 상태와 실패 원인을 기록한다.
- 색인 실패: `FAILED` 또는 재시도 가능한 상태로 남긴다.

## AWS 배포 기본값

```text
Object Storage: S3
Metadata: RDS PostgreSQL
Search index: EBS/EFS 또는 재생성 가능한 local volume
```

초기 AWS 테스트에서는 index directory를 persistent volume으로 두되, rebuild 절차를 반드시 유지한다.
