# Benchmark Plan

이 문서는 성능 측정의 목적, 데이터셋, 지표, 리포트 형식을 정한다.

## 목적

- 색인 처리량과 검색 latency를 수치로 남긴다.
- 구조 변경 전후의 before/after 비교를 만든다.
- 성능 개선 주장이 재현 가능한 조건을 갖도록 한다.

## 초기 Benchmark Runner

JMH보다 custom runner를 먼저 사용한다.

이유:

- 색인부터 검색까지 application-level 흐름을 측정하기 좋다.
- 데이터셋 크기, query 수, topK를 CLI 인자로 바꾸기 쉽다.
- README/문서용 p50, p95, p99 결과를 만들기 좋다.

초기 인자:

```text
--docs
--queries
--topK
--warmup
--seed
```

## 데이터셋

초기 synthetic dataset:

```text
document count: 10,000부터 시작
token vocabulary: 기술 키워드 중심
average document length: 100~500 tokens
seed: 고정
```

확장 단계:

```text
10,000 docs
100,000 docs
1,000,000 docs
```

## 측정 지표

색인:

```text
document count
total token count
indexing elapsed time
docs/sec
average document length
segment count
```

검색:

```text
query count
topK
p50 latency
p95 latency
p99 latency
average latency
max latency
QPS
```

리소스:

```text
heap used before/after
GC count
GC time
index file size
posting list size
```

## 비교 실험

초기 후보:

```text
full sort vs Top-K PriorityQueue
object Posting vs primitive array Posting
in-memory index vs file-based segment
segment merge before vs after
cache disabled vs enabled
```

규칙:

- 비교 실험은 같은 dataset seed, 같은 query seed로 실행한다.
- JVM, CPU, memory, OS, Java version을 기록한다.
- warmup 조건을 기록한다.
- 하나의 변경만 비교한다.

## Report 출력

출력 위치:

```text
build/reports/benchmark/result.json
build/reports/benchmark/result.md
docs/benchmark-result.md
```

`docs/benchmark-result.md`에는 검증된 대표 결과만 옮긴다.

## JMH 도입 기준

JMH는 다음 경우에만 도입한다.

- posting 구조 변경의 allocation 차이를 정밀 비교할 때
- BM25 scorer 같은 작은 hot path를 microbenchmark로 분리할 때
- custom runner로는 noise가 커서 판단하기 어려울 때
