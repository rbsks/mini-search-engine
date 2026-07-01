# Mini Search Engine for Documents

## 1. 프로젝트 개요

### 프로젝트명

**Mini Search Engine for Documents**

### 한 줄 설명

문서 색인, 역색인, BM25 랭킹, Segment Merge를 직접 구현하고 검색 성능을 측정하는 단일 노드 문서 검색 엔진 프로젝트.

### 프로젝트 목표

이 프로젝트의 목표는 Elasticsearch나 Lucene을 단순히 사용하는 것이 아니라, 검색 엔진의 핵심 동작 원리를 직접 구현하는 것이다.

주요 목표는 다음과 같다.

- 문서를 토큰화하고 역색인 구조로 저장한다.
- 검색어에 대해 posting list를 조회하고 후보 문서를 찾는다.
- TF-IDF 또는 BM25 기반으로 검색 결과의 관련도 점수를 계산한다.
- Segment 기반 색인 구조를 구현한다.
- 삭제 문서는 tombstone으로 처리하고, Segment Merge 시 물리 삭제한다.
- 대량 문서 색인 및 검색 성능을 측정한다.
- p50, p95, p99 latency, 색인 처리량, 메모리 사용량 등을 수치화한다.

---

## 2. 이 프로젝트를 하는 이유

대기열 시스템, 선착순 쿠폰, 예약 시스템은 최근 교육 강의나 포트폴리오 주제로 많이 사용되고 있다. 따라서 그대로 구현하면 강의 클론처럼 보일 가능성이 있다.

반면 검색 엔진 구현은 다음 장점이 있다.

- 단순 CRUD가 아니다.
- 자료구조, 알고리즘, I/O, 성능 최적화가 드러난다.
- Java 백엔드 개발자의 기술 깊이를 보여주기 좋다.
- Refinder AI 검색, pgvector, RAG, 검색 성능 개선 경험과 자연스럽게 연결된다.
- 면접에서 깊게 설명할 수 있는 주제가 많다.

이 프로젝트는 “검색 엔진을 만들어봤다”가 아니라, “검색 엔진 내부 구조를 이해하고 성능까지 측정했다”는 것을 보여주는 목적이다.

---

## 3. 프로젝트 범위

### 포함할 것

- 문서 등록 API
- 문서 삭제 API
- 문서 검색 API
- 토크나이저
- 역색인
- Posting List
- TF-IDF 또는 BM25 랭킹
- Top-K 검색
- Segment 저장 구조
- Tombstone 기반 삭제
- Segment Merge
- 검색 성능 측정
- README 성능 리포트

### 포함하지 않을 것

초기 버전에서는 아래 기능은 제외한다.

- 분산 검색
- 샤딩
- 복제
- SQL Parser
- 트랜잭션 엔진
- MVCC
- LLM 답변 생성
- GPU 기반 임베딩 검색
- Elasticsearch 완전 클론

이 프로젝트의 핵심은 “분산 시스템”이 아니라 “단일 노드 검색 엔진의 핵심 구조 구현”이다.

---

## 4. GPU 필요 여부

기본적으로 GPU는 필요하지 않다.

이 프로젝트는 전통적인 키워드 검색 엔진을 구현하는 방향이다.

```text
문서
→ 토큰화
→ 역색인 생성
→ 검색어 토큰화
→ posting list 조회
→ BM25 점수 계산
→ 정렬
→ 결과 반환
```

이 흐름은 대부분 CPU, 메모리, 디스크 I/O 문제다.

GPU가 필요한 경우는 다음과 같다.

- 문서 임베딩 생성
- 쿼리 임베딩 생성
- Dense Vector Search
- Reranker 모델 추론
- LLM 기반 답변 생성

초기 버전에서는 GPU 의존성을 제거하고 검색 엔진 내부 구조에 집중한다.

향후 확장으로 Dense Vector Search나 Hybrid Search를 붙일 수는 있지만, MVP 범위에는 포함하지 않는다.

---

## 5. 핵심 검색 개념

## 5.1 역색인

일반적인 문서 저장 방식은 문서 ID를 기준으로 내용을 찾는다.

```text
doc1 -> "spring boot jpa performance"
doc2 -> "postgresql index tuning"
doc3 -> "spring jpa n+1 problem"
```

하지만 검색 엔진은 검색어로 문서를 찾아야 한다.  
그래서 단어를 기준으로 문서 목록을 저장한다.

```text
spring     -> [doc1, doc3]
jpa        -> [doc1, doc3]
postgresql -> [doc2]
index      -> [doc2]
```

이 구조가 역색인이다.

검색어가 `spring jpa`라면 `spring` posting list와 `jpa` posting list를 조회해서 후보 문서를 찾는다.

---

## 5.2 Posting List

Posting List는 특정 단어가 어떤 문서에 등장했는지를 저장하는 구조다.

단순 구조는 다음과 같다.

```text
term = "spring"

posting list:
doc1, tf=1
doc3, tf=1
doc7, tf=3
```

여기서 `tf`는 term frequency, 즉 해당 문서 안에서 단어가 몇 번 등장했는지를 의미한다.

초기 구현은 객체 기반으로 시작할 수 있다.

```java
public record Posting(
    long docId,
    int termFrequency
) {
}
```

성능 개선 단계에서는 객체 생성을 줄이기 위해 primitive array 구조로 변경할 수 있다.

```text
docIds = [1, 3, 7]
termFrequencies = [1, 1, 3]
```

---

## 5.3 TF-IDF

TF-IDF는 검색 결과의 관련도 점수를 계산하는 대표적인 방식이다.

TF-IDF는 두 가지 요소로 구성된다.

```text
TF = 특정 단어가 한 문서 안에서 얼마나 자주 나왔는가
IDF = 그 단어가 전체 문서 집합에서 얼마나 희귀한가
```

예를 들어 전체 문서가 100,000개 있다고 하자.

```text
spring   -> 50,000개 문서에 등장
pgvector -> 200개 문서에 등장
```

이 경우 `pgvector`가 `spring`보다 더 희귀한 단어다.  
따라서 검색어가 `spring pgvector`라면 `pgvector`가 포함된 문서를 더 중요하게 볼 수 있다.

간단히 말하면:

```text
자주 나온 단어일수록 점수 증가
하지만 전체 문서에 너무 흔한 단어면 중요도 감소
```

단순 공식은 다음과 같다.

```text
score = TF * IDF
```

---

## 5.4 BM25

BM25는 TF-IDF를 실전 검색에 더 적합하게 개선한 랭킹 방식이다.

TF-IDF의 단점은 다음과 같다.

### 문제 1. 단어 반복에 점수가 과하게 증가할 수 있음

```text
문서 A: "spring boot 성능 개선"
문서 B: "spring spring spring spring spring spring"
```

단순 TF 기준으로는 B가 더 높은 점수를 받을 수 있다.  
하지만 실제 검색 품질 관점에서는 B가 좋은 문서라고 보기 어렵다.

BM25는 term frequency saturation을 적용한다.

```text
단어가 1번 → 2번 나오는 것은 의미 있음
단어가 20번 → 21번 나오는 것은 큰 의미 없음
```

즉, 단어 반복 횟수가 늘어날수록 점수 증가폭을 줄인다.

### 문제 2. 긴 문서가 무조건 유리할 수 있음

긴 문서는 검색어가 우연히 포함될 가능성이 높다.

```text
문서 A: "JPA N+1 문제 해결 방법"
문서 B: 10,000자짜리 긴 글 안에 "JPA", "N+1"이 각각 한 번 등장
```

검색어가 `jpa n+1`이라면 문서 A가 더 관련도가 높아야 한다.  
BM25는 문서 길이 정규화를 통해 긴 문서가 과도하게 유리해지는 문제를 줄인다.

BM25는 다음 요소를 반영한다.

```text
TF(q, D) = 검색어 q가 문서 D에 등장한 횟수
IDF(q) = 검색어 q가 전체 문서에서 얼마나 희귀한지
문서 길이 = 현재 문서의 토큰 수
평균 문서 길이 = 전체 문서의 평균 토큰 수
k1 = 단어 반복 횟수의 영향도 조절
b = 문서 길이 보정 강도 조절
```

일반적으로 다음 기본값을 사용한다.

```text
k1 = 1.2
b = 0.75
```

포트폴리오에서는 TF-IDF보다 BM25를 구현하는 것이 더 좋다.

이력서에서 다음과 같이 표현할 수 있다.

```text
TF, IDF, 문서 길이 정규화, term frequency saturation을 반영한 BM25 기반 랭킹 알고리즘 구현
```

---

## 6. Java로 구현해도 되는 이유

Java로 search core를 구현해도 충분히 의미 있다.

실제로 Apache Lucene은 Java 기반 검색 라이브러리이고, Elasticsearch, Solr, OpenSearch도 Lucene을 검색 core로 사용한다.

검색 엔진 성능은 언어보다 다음 요소에 더 크게 좌우된다.

- 역색인 구조
- Posting List 저장 방식
- Segment 구조
- 파일 I/O
- 캐싱
- Top-K 추출 방식
- Segment Merge 전략
- 객체 생성량
- GC 부담
- 압축 여부

따라서 이 프로젝트는 Java로 구현하는 것이 오히려 자연스럽다.

특히 Java 백엔드 개발자 포트폴리오라면 다음 메시지를 줄 수 있다.

```text
Java/Spring 백엔드 개발자가 검색 엔진 내부 구조를 직접 구현하고,
성능 병목을 측정하여 개선했다.
```

---

## 7. 권장 기술 스택

### Language

- Java 21

또는 Kotlin을 사용할 수 있지만, 검색 core의 성능과 자료구조 구현을 명확하게 보여주기 위해 Java 21을 우선 추천한다.

### Framework

- Spring Boot

Spring Boot는 API 서버 영역에만 사용한다.  
검색 core는 Spring에 의존하지 않는 순수 Java 모듈로 구현한다.

### Storage

- 초기 버전: in-memory index
- 이후 버전: file-based segment storage
- PostgreSQL은 원본 문서 메타데이터 저장용으로만 선택적으로 사용

### Benchmark

- JMH
- k6
- custom benchmark runner

### Observability

- Prometheus
- Grafana
- Micrometer

---

## 8. 모듈 구조

권장 모듈 구조는 다음과 같다.

```text
mini-search-engine
├─ search-api
│  └─ Spring Boot API 서버
│
├─ search-core
│  ├─ analyzer
│  ├─ index
│  ├─ ranking
│  ├─ query
│  ├─ segment
│  └─ search
│
├─ search-storage
│  └─ file-based segment storage
│
├─ search-benchmark
│  └─ 성능 측정 코드
│
└─ docs
   ├─ architecture.md
   ├─ benchmark-result.md
   └─ design-decisions.md
```

---

## 9. search-core 설계

`search-core`는 Spring 의존성이 없는 순수 Java 모듈로 구현한다.

### 주요 컴포넌트

```text
Analyzer
Tokenizer
TokenFilter
InvertedIndex
PostingList
DocumentStore
Segment
SegmentWriter
SegmentReader
SegmentMerger
BM25Scorer
SearchEngine
QueryParser
TopKCollector
```

### 책임 분리

```text
Analyzer
- 문서를 토큰화한다.
- 소문자 변환, 불용어 제거, 정규화 등을 처리한다.

InvertedIndex
- term -> posting list 구조를 관리한다.

PostingList
- 특정 term이 등장한 문서 목록과 term frequency를 저장한다.

BM25Scorer
- 검색어와 문서의 관련도 점수를 계산한다.

Segment
- 일정 단위로 flush된 불변 색인 파일이다.

SegmentMerger
- 작은 segment들을 병합한다.
- tombstone 문서를 제거한다.

SearchEngine
- 검색 요청을 받아 segment들을 조회하고 결과를 반환한다.
```

---

## 10. API 설계

### 문서 등록

```http
POST /documents
Content-Type: application/json

{
  "title": "JPA N+1 문제 해결",
  "content": "JPA에서 N+1 문제가 발생하는 원인과 fetch join을 통한 해결 방법...",
  "tags": ["java", "spring", "jpa"]
}
```

### 문서 삭제

```http
DELETE /documents/{documentId}
```

삭제는 즉시 물리 삭제하지 않고 tombstone으로 처리한다.

### 문서 검색

```http
GET /search?q=spring jpa&page=0&size=20
```

응답 예시:

```json
{
  "query": "spring jpa",
  "totalHits": 142,
  "tookMs": 8,
  "results": [
    {
      "documentId": 1,
      "title": "JPA N+1 문제 해결",
      "score": 12.41,
      "highlight": "...JPA에서 N+1 문제가 발생하는 원인..."
    }
  ]
}
```

---

## 11. Segment 구조

Segment는 일정량의 문서가 색인되었을 때 생성되는 불변 인덱스 단위다.

```text
segment_0001
segment_0002
segment_0003
```

문서 등록 흐름은 다음과 같다.

```text
문서 등록
→ memory buffer에 색인
→ 일정 문서 수 또는 메모리 크기 초과
→ segment flush
→ 새로운 segment 생성
```

검색 흐름은 다음과 같다.

```text
검색 요청
→ 모든 segment 대상 검색
→ segment별 top-k 후보 수집
→ 전체 top-k merge
→ 결과 반환
```

삭제 흐름은 다음과 같다.

```text
문서 삭제 요청
→ deleted_doc_ids에 tombstone 기록
→ 검색 결과에서 제외
→ segment merge 시 물리 삭제
```

Segment Merge 흐름은 다음과 같다.

```text
작은 segment 여러 개 선택
→ tombstone 문서 제외
→ posting list 재구성
→ 큰 segment 생성
→ 기존 segment 제거
```

---

## 12. Top-K 검색

검색 결과 전체를 정렬하면 비용이 커진다.

```text
전체 정렬: O(N log N)
Top-K 유지: O(N log K)
```

검색 결과 상위 20개만 필요하다면 전체 결과를 다 정렬하지 않고, PriorityQueue를 사용해 상위 K개만 유지한다.

```java
PriorityQueue<SearchHit> topK = new PriorityQueue<>(Comparator.comparing(SearchHit::score));
```

이 방식은 검색 결과 후보가 많을수록 효과가 크다.

---

## 13. 성능 최적화 포인트

처음부터 모든 최적화를 적용하지 않는다.  
포트폴리오에서는 Before/After를 보여주는 것이 더 중요하다.

### 1차 구현

- 객체 기반 Posting
- in-memory index
- 단순 BM25 계산
- 전체 segment 순회

### 2차 개선

- Posting 객체를 primitive array로 변경
- Top-K PriorityQueue 적용
- field boost 적용
- 검색 결과 캐시 적용

### 3차 개선

- file-based segment 저장
- Segment Merge 적용
- tombstone 제거
- mmap 또는 buffered I/O 검토
- 검색 latency 및 GC allocation 비교

---

## 14. 측정할 지표

README에는 반드시 수치를 남긴다.

### 색인 지표

```text
문서 수
총 토큰 수
평균 문서 길이
색인 소요 시간
초당 색인 문서 수
segment 개수
segment flush 횟수
```

### 검색 지표

```text
검색 요청 수
p50 latency
p95 latency
p99 latency
평균 latency
최대 latency
QPS
검색 결과 수
```

### 리소스 지표

```text
heap 사용량
GC 횟수
GC pause time
파일 크기
posting list 크기
cache hit ratio
```

### 비교 실험

```text
객체 기반 Posting vs primitive array Posting
전체 정렬 vs Top-K PriorityQueue
Segment Merge 전 vs 후
Cache 적용 전 vs 후
```

---

## 15. 구현 로드맵

## Phase 1. 기본 검색 엔진

목표: in-memory 기반으로 검색 엔진의 기본 흐름을 완성한다.

- Document 모델 정의
- Analyzer 구현
- Tokenizer 구현
- InvertedIndex 구현
- PostingList 구현
- TF-IDF 또는 BM25 구현
- Search API 구현
- Top-K 검색 구현

완료 기준:

```text
문서 등록 가능
검색 가능
BM25 점수 기준 정렬 가능
상위 K개 결과 반환 가능
```

---

## Phase 2. Segment 구조 도입

목표: 인덱스를 Segment 단위로 분리한다.

- Memory buffer 구현
- SegmentWriter 구현
- SegmentReader 구현
- Segment metadata 관리
- 여러 segment 대상 검색 구현

완료 기준:

```text
일정 문서 수마다 segment 생성
여러 segment 검색 가능
segment별 결과를 merge해서 top-k 반환
```

---

## Phase 3. 삭제와 Merge

목표: 검색 엔진다운 삭제 및 병합 구조를 구현한다.

- Tombstone 관리
- 삭제 문서 검색 제외
- SegmentMerger 구현
- 병합 시 삭제 문서 제거
- 병합 전후 성능 비교

완료 기준:

```text
삭제 요청은 tombstone 처리
검색 결과에서 삭제 문서 제외
merge 이후 삭제 문서가 물리적으로 제거됨
```

---

## Phase 4. 성능 측정

목표: 포트폴리오에 넣을 수 있는 수치를 만든다.

- 더미 문서 10만 건 생성
- 색인 시간 측정
- 검색 latency 측정
- p50, p95, p99 계산
- GC allocation 측정
- README에 결과 정리

완료 기준:

```text
성능 테스트 결과가 benchmark-result.md에 기록됨
개선 전후 수치가 비교됨
```

---

## Phase 5. 고도화

선택 기능이다.

- Field Boost
- Highlighting
- Stopword 제거
- Synonym 처리
- 검색 결과 캐시
- Boolean Query
- Phrase Query
- Pagination 최적화
- Prometheus/Grafana 대시보드

---

## 16. README에 넣을 성능 결과 예시

실제 구현 후 아래 형식으로 채운다.

```text
테스트 환경
- CPU:
- Memory:
- JVM:
- Java:
- Dataset:
- Document Count:

색인 결과
- 전체 문서 수: 100,000
- 색인 소요 시간: xx sec
- 초당 색인 처리량: xx docs/sec
- 생성 segment 수: xx

검색 결과
- 요청 수: 10,000
- p50 latency: xx ms
- p95 latency: xx ms
- p99 latency: xx ms
- max latency: xx ms

개선 결과
- 전체 정렬 → Top-K PriorityQueue 적용
- p95 latency: xx ms → yy ms
- 개선율: zz%

- Posting 객체 → primitive array 적용
- heap allocation: xx MB → yy MB
- GC pause: xx ms → yy ms
```

---

## 17. 이력서 문구 초안

```text
개인 프로젝트 | 문서 검색 엔진 구현 및 성능 최적화

- 문서 색인, 토큰화, 역색인, Posting List, BM25 랭킹을 직접 구현한 단일 노드 검색 엔진 개발
- 불변 Segment 구조와 Tombstone 기반 삭제 방식을 적용하고, 백그라운드 Segment Merge로 검색 대상 인덱스 수를 최적화
- TF, IDF, 문서 길이 정규화, term frequency saturation을 반영한 BM25 기반 관련도 점수 계산 구현
- 검색 결과 전체 정렬 대신 PriorityQueue 기반 Top-K 수집 방식을 적용해 검색 후보 증가 시 정렬 비용 절감
- 대량 문서 색인 및 검색 부하 테스트를 통해 p95 latency, 색인 처리량, 메모리 사용량을 측정하고 개선 전후 수치를 README에 정리
```

---

## 18. 면접에서 설명할 포인트

### 왜 이 프로젝트를 했는가?

검색 엔진을 단순히 사용하는 것을 넘어, 역색인, 랭킹, 세그먼트, 삭제, 병합 같은 내부 구조를 직접 구현해보고 싶었다.  
Refinder에서 AI 검색과 pgvector 기반 검색을 다루면서 검색 품질과 성능이 단순 쿼리 문제가 아니라 인덱스 구조, 랭킹 방식, 후보 문서 수집 방식에 영향을 많이 받는다는 것을 경험했고, 이를 더 깊게 이해하기 위해 진행했다.

### 왜 Java로 구현했는가?

주력 언어가 Java이고, 실제로 Lucene도 Java 기반 검색 라이브러리다.  
검색 엔진 성능은 언어보다 인덱스 구조, posting list, segment, I/O, 캐싱, Top-K 수집 방식에 크게 좌우된다고 판단했다.  
또한 Java에서 객체 생성과 GC가 검색 성능에 어떤 영향을 주는지 직접 측정할 수 있다는 점도 장점이라고 봤다.

### 왜 BM25를 사용했는가?

단순 TF-IDF는 단어 반복이 많은 문서나 길이가 긴 문서에 점수가 과하게 부여될 수 있다.  
BM25는 term frequency saturation과 문서 길이 정규화를 반영하기 때문에 실제 검색 랭킹에 더 적합하다고 판단했다.

### 왜 Segment 구조를 사용했는가?

문서가 추가될 때마다 기존 인덱스를 매번 수정하면 비용이 커진다.  
따라서 일정량의 문서를 불변 Segment로 flush하고, 검색 시 여러 Segment를 조회하도록 설계했다.  
삭제는 tombstone으로 처리하고, 이후 Segment Merge 시 물리 삭제하는 방식으로 구현했다.

---

## 19. Codex에게 줄 작업 지시 예시

아래 문장을 Codex에게 그대로 전달해도 된다.

```text
Java 21 기반으로 Mini Search Engine for Documents 프로젝트를 시작하려고 한다.

목표는 Elasticsearch나 Lucene을 사용하는 것이 아니라, 검색 엔진의 핵심 구조를 직접 구현하는 것이다.

초기 MVP 범위는 다음과 같다.

1. search-core 모듈은 Spring 의존성 없는 순수 Java 모듈로 만든다.
2. Document, Analyzer, Tokenizer, InvertedIndex, PostingList, BM25Scorer, SearchEngine을 구현한다.
3. 문서를 등록하면 토큰화 후 term -> posting list 구조로 역색인을 만든다.
4. 검색어가 들어오면 query term별 posting list를 조회하고 BM25 점수를 계산한다.
5. 검색 결과는 score 기준 상위 K개만 PriorityQueue로 유지한다.
6. search-api 모듈은 Spring Boot로 만들고, 문서 등록 API와 검색 API를 제공한다.
7. 초기 버전은 in-memory index로 구현한다.
8. 이후 단계에서 Segment, Tombstone, Segment Merge를 추가할 예정이다.

먼저 멀티모듈 Gradle 프로젝트 구조와 핵심 도메인 클래스 설계를 제안해줘.
```

---

## 20. 첫 번째 구현 목표

처음부터 Segment Merge까지 가지 말고 아래까지만 먼저 완성한다.

```text
문서 등록
→ 토큰화
→ in-memory 역색인
→ BM25 점수 계산
→ Top-K 검색
→ 검색 API 응답
```

이후에 다음 순서로 확장한다.

```text
1. in-memory 검색 엔진
2. Segment 구조
3. Tombstone 삭제
4. Segment Merge
5. 성능 측정
6. 최적화
7. README 정리
```

---

## 21. 최종 방향

이 프로젝트의 핵심 메시지는 다음과 같다.

```text
검색 엔진을 단순히 사용하는 개발자가 아니라,
검색 엔진의 핵심 구조를 직접 구현하고
성능 병목을 측정해 개선할 수 있는 백엔드 개발자
```

따라서 구현보다 중요한 것은 다음이다.

- 구조를 설명할 수 있어야 한다.
- 왜 그렇게 설계했는지 말할 수 있어야 한다.
- 성능 수치를 남겨야 한다.
- 개선 전후 비교가 있어야 한다.
- README가 이력서처럼 정리되어 있어야 한다.
