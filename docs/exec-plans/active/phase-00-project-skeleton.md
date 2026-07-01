# Phase 00. Project Skeleton

목표: 멀티모듈 구조가 IDE와 Gradle에서 정상 인식되는지 확인한다.

## TODO

- [ ] IntelliJ에서 Gradle project를 다시 import한다.
- [ ] `search-api`, `search-core`, `search-storage`, `search-benchmark` 모듈이 보이는지 확인한다.
- [ ] JDK 21로 `.\gradlew.bat projects`를 실행한다.
- [ ] JDK 21로 `.\gradlew.bat test`를 실행한다.
- [ ] 문제가 있으면 Gradle sync/build output을 Codex에게 보여주고 리뷰를 요청한다.

## Review Checkpoint

- 모듈 의존 방향이 `docs/design-docs/layered-architecture.md`와 맞는지 확인한다.

## Done Criteria

- Gradle projects 출력에 4개 하위 모듈이 보인다.
- 전체 `test`가 통과한다.
