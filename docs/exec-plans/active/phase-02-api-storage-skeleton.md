# Phase 02. API and Storage Skeleton

목표: 업로드/검색 API 골격과 Object Storage 추상화를 만든다.

## search-api Upload/Search Skeleton

- [ ] `document` 도메인 패키지를 만든다.
- [ ] `presentation`, `application`, `domain`, `infrastructure` 패키지 구조를 만든다.
- [ ] `POST /documents` multipart API skeleton을 만든다.
- [ ] 첫 지원 파일 타입은 `txt`, `md`로 제한한다.
- [ ] 업로드 요청 validation을 추가한다.
- [ ] `GET /search` API skeleton을 만든다.

Review checkpoint:

- Controller가 application service만 호출하는지 Codex에게 리뷰받는다.

## search-storage Skeleton

- [ ] `ObjectStorage` 인터페이스를 만든다.
- [ ] `StoredObject` record를 만든다.
- [ ] `put`, `get`, `exists`, `delete` 계약을 정한다.
- [ ] 아직 실제 SDK 구현이 부담되면 in-memory fake로 application 흐름을 먼저 검증한다.
- [ ] 이후 MinIO 구현과 통합 테스트를 추가한다.

Review checkpoint:

- Object Storage abstraction이 S3/MinIO 교체 가능하게 생겼는지 Codex에게 리뷰받는다.

## Done Criteria

- API skeleton이 컴파일된다.
- storage abstraction이 `search-api`에서 구현체 교체 가능하게 주입된다.
