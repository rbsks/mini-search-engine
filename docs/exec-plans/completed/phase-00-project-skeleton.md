# Phase 00. Project Skeleton

목표: 멀티모듈 구조가 IDE와 Gradle에서 정상 인식되는지 확인한다.

상태: Completed

완료일: 2026-07-06

## Completion Record

- 실제 구현 범위: Gradle 멀티모듈 skeleton을 `search-api`, `search-core`, `search-storage`, `search-benchmark`로 구성했다.
- 실제 구현 범위: 기존 Kotlin/Spring Boot 앱은 `search-api` 모듈 진입점으로 유지했다.
- 실제 구현 범위: `search-core`, `search-storage`, `search-benchmark`는 후속 phase 구현을 위한 독립 모듈로 인식된다.
- 바뀐 결정: 없음. 모듈러 모놀리식, Java 21 기반 `search-core`, Kotlin/Spring Boot 기반 `search-api` 방향을 유지한다.
- 검증 명령: `.\gradlew.bat projects --console plain`로 `:search-api`, `:search-benchmark`, `:search-core`, `:search-storage` 4개 하위 모듈을 확인했다.
- 검증 명령: `.\gradlew.bat test`가 `BUILD SUCCESSFUL`로 통과했다.
- 남은 follow-up: Phase 01에서 `search-core` analyzer, inverted index, BM25, Top-K MVP를 구현한다.

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
