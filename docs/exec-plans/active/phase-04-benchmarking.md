# Phase 04. Benchmarking

목표: 포트폴리오에 기록할 수 있는 재현 가능한 성능 측정 러너를 만든다.

## TODO

- [ ] `search-benchmark`에 `BenchmarkRunner`를 만든다.
- [ ] 고정 seed 기반 synthetic document generator를 만든다.
- [ ] 고정 seed 기반 query generator를 만든다.
- [ ] 색인 시간과 docs/sec를 측정한다.
- [ ] 검색 p50/p95/p99/max latency를 측정한다.
- [ ] `build/reports/benchmark/result.json`을 생성한다.
- [ ] 대표 결과만 `docs/benchmark-result.md`에 정리한다.

## Review Checkpoint

- 측정 조건과 before/after 비교 가능성을 Codex에게 리뷰받는다.

## Done Criteria

- 같은 seed로 반복 실행 가능한 benchmark 결과가 생성된다.
- 데이터셋 크기, JVM, query 수, topK가 결과에 기록된다.
