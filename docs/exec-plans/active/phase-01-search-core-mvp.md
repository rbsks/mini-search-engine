# Phase 01. search-core MVP

목표: Spring 없는 Java 21 core에서 analyzer, inverted index, BM25, Top-K 검색 흐름을 완성한다.

## 1. Analyzer

- [ ] `com.minisearchengine.core.analyzer` 패키지를 만든다.
- [ ] `Analyzer` 인터페이스를 만든다.
- [ ] lowercase + 문자/숫자 기반 기본 Analyzer를 만든다.
- [ ] `TokenFilter` 확장 지점을 둘지 판단한다.
- [ ] Analyzer 단위 테스트를 만든다.

Review checkpoint:

- Analyzer가 index 저장 구조를 모르도록 분리됐는지 Codex에게 리뷰를 요청한다.

## 2. Inverted Index

- [ ] `SearchDocument` record를 만든다.
- [ ] `Posting` record를 만든다.
- [ ] `PostingList`를 객체 기반으로 만든다.
- [ ] `InvertedIndex`를 만든다.
- [ ] 문서별 token length와 전체 document count를 관리한다.
- [ ] term frequency와 document frequency 테스트를 만든다.

Review checkpoint:

- posting list 책임과 document statistics 책임이 섞이지 않았는지 리뷰받는다.

## 3. BM25 Ranking

- [ ] `Bm25Scorer`를 만든다.
- [ ] `k1=1.2`, `b=0.75` 기본값을 적용한다.
- [ ] IDF 공식은 `ln(1 + (N - df + 0.5) / (df + 0.5))`로 시작한다.
- [ ] 작은 고정 코퍼스로 BM25 점수 테스트를 만든다.
- [ ] 긴 문서 보정과 term frequency saturation이 드러나는 테스트를 만든다.

Review checkpoint:

- BM25 공식, 테스트 데이터, 부동소수점 허용 오차를 Codex에게 리뷰받는다.

## 4. Search Engine and Top-K

- [ ] `SearchRequest` record를 만든다.
- [ ] `SearchHit` record를 만든다.
- [ ] `TopKCollector`를 PriorityQueue 기반으로 만든다.
- [ ] `SearchEngine.index(document)`를 만든다.
- [ ] `SearchEngine.search(request)`를 만든다.
- [ ] 검색 후보는 query term posting list의 OR union으로 시작한다.
- [ ] 결과 정렬은 `score desc`, 동점 시 `documentId asc`로 고정한다.
- [ ] 작은 고정 코퍼스 ranking 테스트를 만든다.

Review checkpoint:

- 검색 흐름과 정렬 안정성을 Codex에게 리뷰받는다.

## Done Criteria

- `.\gradlew.bat :search-core:test`가 통과한다.
- 작은 고정 코퍼스에서 검색 결과 순서가 결정적으로 검증된다.
