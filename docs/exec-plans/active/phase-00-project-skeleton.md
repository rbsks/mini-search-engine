# Phase 00. Project Skeleton

목표: 멀티모듈 구조가 IDE와 Gradle에서 정상 인식되는지 확인한다.

## TODO

- [X] IntelliJ에서 Gradle project를 다시 import한다.
- [X] `search-api`, `search-core`, `search-storage`, `search-benchmark` 모듈이 보이는지 확인한다.
- [X] 터미널에서 JDK 21을 현재 세션에 적용한다.
- [X] JDK 21로 `.\gradlew.bat projects`를 실행한다.
- [X] JDK 21로 `.\gradlew.bat test`를 실행한다.
- [X] 문제가 있으면 Gradle sync/build output을 Codex에게 보여주고 리뷰를 요청한다.

## Review Checkpoint

- Gradle 멀티모듈이 정상 인식되는지 확인한다.
- 루트 프로젝트에 불필요한 source set이나 Spring Boot 실행 task가 생기지 않았는지 확인한다.
- 기존 Spring Boot 앱이 `search-api` 모듈 아래에서 정상 컴파일되는지 확인한다.

## Done Criteria

- Gradle projects 출력에 4개 하위 모듈이 보인다.
- 전체 `test`가 통과한다.
