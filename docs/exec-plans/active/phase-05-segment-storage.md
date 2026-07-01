# Phase 05. Segment Storage

목표: in-memory index 이후 file-based segment 구조를 도입한다.

## TODO

- [ ] in-memory index가 안정화된 뒤 시작한다.
- [ ] segment manifest 초안을 만든다.
- [ ] file-based segment writer를 만든다.
- [ ] segment reader를 만든다.
- [ ] 앱 재시작 후 manifest를 읽어 검색 가능한 상태를 복구한다.
- [ ] RDB/Object Storage 기반 full rebuild 절차를 만든다.

## Review Checkpoint

- segment file format을 너무 일찍 복잡하게 만들지 않았는지 Codex에게 리뷰받는다.

## Done Criteria

- 앱 재시작 후 기존 segment를 읽어 검색할 수 있다.
- index directory 삭제 시 rebuild 경로가 문서화되어 있다.
