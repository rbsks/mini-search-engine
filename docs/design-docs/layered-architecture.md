# Layered Architecture

이 문서는 모듈러 모놀리식 구조에서 계층과 의존 방향을 고정한다. 구현 중 애매하면 이 문서의 의존 규칙을 우선한다.

## 기본 원칙

- 의존성은 바깥 계층에서 안쪽 계층으로만 흐른다.
- `search-core`는 검색 엔진 라이브러리 역할이며 Spring, RDB, Object Storage를 모른다.
- `search-api`는 HTTP/API, 트랜잭션, 유스케이스 오케스트레이션을 담당한다.
- `search-storage`는 S3-compatible Object Storage 접근을 캡슐화한다.
- `search-benchmark`는 운영 코드에 의존 방향을 만들지 않고, 측정 대상 모듈을 호출하는 실행 모듈이다.

## 모듈 의존 방향

```text
search-api
├─ depends on search-core
├─ depends on search-storage
└─ depends on Spring/JPA/RDB adapters

search-storage
└─ depends on S3-compatible SDKs

search-benchmark
├─ depends on search-core
└─ optionally depends on search-storage for storage benchmarks

search-core
└─ depends only on Java standard library and test libraries
```

금지 의존성:

```text
search-core -> search-api
search-core -> search-storage
search-core -> Spring/JPA/RDB/S3/MinIO
search-storage -> search-api
search-storage -> search-core
```

## search-api 내부 계층

```text
presentation
→ application
→ domain

infrastructure
→ application ports/domain
```

- `presentation`: Controller, request/response DTO, HTTP validation, status mapping
- `application`: use case service, transaction boundary, indexing workflow orchestration
- `domain`: document metadata, indexing status, storage key policy 같은 비즈니스 규칙
- `infrastructure`: JPA repository, Object Storage adapter wiring, search-core adapter, external client config

규칙:

- Controller는 repository, Object Storage client, `SearchEngine`을 직접 호출하지 않는다.
- Application service는 use case 흐름을 조합하고, HTTP DTO를 직접 사용하지 않는다.
- Domain은 Spring annotation, JPA entity, storage SDK type에 의존하지 않는다.
- Infrastructure는 바깥 구현 세부사항을 감싸고 application/domain 쪽으로 타입을 흘려보내지 않는다.

## search-core 내부 계층

```text
search
├─ analyzer
├─ query
├─ index
└─ ranking

segment
└─ index model/statistics
```

권장 패키지:

```text
com.minisearchengine.core.analyzer
com.minisearchengine.core.index
com.minisearchengine.core.query
com.minisearchengine.core.ranking
com.minisearchengine.core.search
com.minisearchengine.core.segment
```

규칙:

- `analyzer`는 텍스트를 토큰으로 바꾸며 index 저장 구조를 모른다.
- `index`는 term, posting list, document statistics를 관리한다.
- `ranking`은 BM25 점수 계산에 집중하고 저장소나 API 응답을 모른다.
- `query`는 검색어 파싱과 query term 표현을 담당한다.
- `search`는 analyzer, index, ranking, collector를 조합한다.
- `segment`는 file-based index가 도입될 때 불변 segment 읽기/쓰기 책임을 가진다.
- `index`, `ranking`, `analyzer`, `query`가 서로 순환 의존하지 않게 유지한다.

## 데이터 흐름

```text
POST /documents
→ presentation
→ application upload use case
→ Object Storage에 원본 저장
→ RDB에 metadata 저장
→ 텍스트 추출
→ Object Storage에 extracted text 저장
→ search-core index 호출
→ index_status 갱신
```

```text
GET /search
→ presentation
→ application search use case
→ search-core search 호출
→ RDB에서 metadata 조회
→ response DTO 변환
```

## 트랜잭션과 외부 저장소

- RDB 트랜잭션은 application service에서 시작하고 끝낸다.
- Object Storage와 RDB를 하나의 트랜잭션으로 묶으려 하지 않는다.
- Object Storage 저장 성공 후 RDB 저장 실패가 나면 보상 삭제 또는 FAILED 상태 기록 전략을 명시한다.
- 검색 색인 갱신 실패는 문서 metadata를 `FAILED` 또는 재시도 가능한 상태로 남긴다.

## 테스트 경계

- `search-core`: Spring 없는 순수 단위 테스트를 우선한다.
- `search-storage`: MinIO/Testcontainers 기반 통합 테스트를 둔다.
- `search-api`: Controller 테스트와 업로드-색인-검색 E2E 테스트를 둔다.
- `search-benchmark`: 고정 seed로 재현 가능한 측정만 남긴다.
