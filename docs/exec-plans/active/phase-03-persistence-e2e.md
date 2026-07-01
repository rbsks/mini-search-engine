# Phase 03. Persistence and Upload-to-Search E2E

목표: 업로드 파일 저장, metadata 저장, 텍스트 추출, 색인, 검색까지 이어지는 최소 흐름을 만든다.

## Metadata Persistence

- [ ] PostgreSQL 의존성과 설정을 추가한다.
- [ ] migration 도구를 Flyway 또는 Liquibase 중 하나로 정한다.
- [ ] `documents` 테이블을 만든다.
- [ ] `index_status` 상태 전이를 구현한다.
- [ ] 업로드 성공/실패 흐름 테스트를 만든다.

Review checkpoint:

- RDB와 Object Storage 사이 보상 처리 전략을 Codex에게 리뷰받는다.

## Upload to Search E2E

- [ ] 업로드 파일을 Object Storage에 저장한다.
- [ ] metadata를 RDB에 저장한다.
- [ ] txt/md 텍스트를 추출한다.
- [ ] extracted text를 Object Storage에 저장한다.
- [ ] `search-core`로 색인한다.
- [ ] 검색 API에서 업로드 문서가 반환되는 E2E 테스트를 만든다.

Review checkpoint:

- E2E 테스트가 너무 무겁거나 깨지기 쉬운지 Codex에게 리뷰받는다.

## Done Criteria

- `txt` 또는 `md` 파일 업로드 후 검색 결과에 해당 문서가 나온다.
- 실패 상태가 RDB에 명확히 남는다.
