# Code Conventions

이 문서는 Java 21 `search-core`와 Kotlin/Spring `search-api`를 함께 개발할 때의 코드 기준을 정한다.

## 공통 기준

- 들여쓰기는 space 4칸을 사용한다.
- 한 줄은 가능하면 120자 안쪽으로 유지한다.
- wildcard import를 사용하지 않는다.
- 의미 없는 축약어를 피하고, 검색 도메인 용어는 일관되게 사용한다.
- public API는 의도가 드러나는 이름을 사용하고, 구현 세부사항 이름을 노출하지 않는다.
- 주석은 "무엇"보다 "왜"를 설명할 때만 짧게 남긴다.
- 로그에는 원본 문서 본문, 추출 텍스트, 민감한 파일명을 그대로 남기지 않는다.

## Java 21 search-core

- 값 객체는 가능하면 `record`로 만든다.
- 불변으로 충분한 타입은 `final class`로 만든다.
- 생성자에서 필수 값은 `Objects.requireNonNull`로 검증한다.
- 컬렉션을 외부에 반환할 때는 변경 가능한 내부 컬렉션을 그대로 노출하지 않는다.
- `Optional`은 반환 타입에만 사용하고, 필드나 파라미터로 사용하지 않는다.
- checked exception은 I/O 경계에서만 사용하고, core 도메인 규칙에는 런타임 예외를 사용한다.
- 성능 최적화 전에는 객체 기반 구조를 우선하고, primitive array 전환은 benchmark 결과를 근거로 한다.
- `search-core`에는 Spring annotation, JPA annotation, storage SDK type을 넣지 않는다.

## Kotlin search-api

- Controller, DTO, configuration, application service는 Kotlin으로 작성한다.
- 생성자 주입을 사용하고 field injection을 사용하지 않는다.
- `!!` 사용을 피한다.
- `lateinit`은 테스트 코드 외에는 사용하지 않는다.
- request/response DTO는 `data class`를 기본으로 한다.
- nullable 타입은 실제로 값이 없을 수 있는 경우에만 사용한다.
- Spring transaction boundary는 application service에 둔다.
- Controller는 HTTP 변환만 담당하고 비즈니스 흐름을 직접 구현하지 않는다.

## 패키지와 이름

- 패키지는 모두 소문자로 작성한다.
- 테스트 클래스명은 대상 + `Test` 또는 시나리오 + `Test` 형식을 사용한다.
- 통합 테스트는 대상 + `IntegrationTest` 형식을 사용한다.
- benchmark 실행 클래스는 대상 + `BenchmarkRunner` 형식을 사용한다.

예시:

```text
com.minisearchengine.core.ranking.Bm25Scorer
com.minisearchengine.core.search.SearchEngine
com.minisearchengine.api.document.presentation.DocumentController
com.minisearchengine.api.document.application.UploadDocumentService
```

## 테스트 기준

- 검색 랭킹 테스트는 작은 고정 코퍼스를 사용한다.
- 랜덤 데이터는 반드시 seed를 고정한다.
- latency나 처리량 수치는 테스트 assertion으로 고정하지 않는다.
- BM25 점수는 부동소수점 오차를 고려해 허용 오차를 둔다.
- 회귀 방지 테스트는 버그 원인과 기대 동작이 드러나는 이름으로 작성한다.

## 오류 처리

- 사용자 입력 오류는 API 계층에서 4xx로 변환한다.
- Object Storage, RDB, file segment I/O 실패는 application 계층에서 상태 전이를 명확히 남긴다.
- `search-core`는 HTTP status나 API error response를 모른다.
- 예외 메시지에는 storage credential, presigned URL, 원문 전체를 포함하지 않는다.

## 성능 측정 기준

- 성능 개선 주장은 benchmark 결과 없이 문서에 쓰지 않는다.
- before/after 비교에는 데이터셋 크기, JVM, warmup, query 수, topK를 함께 남긴다.
- microbenchmark가 필요한 경우에만 JMH를 도입한다.
- application-level 성능은 custom runner 또는 k6로 측정한다.
