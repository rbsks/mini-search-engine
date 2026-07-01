# Codex Working Harness

이 문서는 Codex가 매 작업 전에 빠르게 읽고 프로젝트 맥락을 회복하기 위한 작업 하네스다.

## 한 줄 목표

업로드한 문서를 Object Storage에 저장하고, 추출 텍스트를 기반으로 Java 21 `search-core`가 Lucene-inspired file segment 검색 색인을 직접 관리하는 미니 문서 검색 엔진을 만든다.

## 현재 합의된 방향

- 모듈러 모놀리식으로 간다.
- API 서버는 Kotlin/Spring Boot를 유지한다.
- 검색 core는 Java 21 순수 모듈로 분리한다.
- 로컬은 MinIO, AWS는 S3를 사용한다.
- 원본 파일과 추출 텍스트는 Object Storage에 둔다.
- RDB는 문서 메타데이터와 처리 상태만 관리한다.
- 검색 색인은 source of truth가 아니라 재생성 가능한 파생 데이터다.
- Lucene과 비슷한 segment 기반 방향은 좋지만, Lucene 클론이 목표는 아니다.

## 우선 만들 작업 자료

- 루트 `AGENTS.md`: 에이전트 작업 규칙
- `docs/index.md`: 전체 문서 지도
- `docs/codex-harness.md`: 현재 컨텍스트 요약
- `docs/design-docs/index.md`: 설계 문서 지도
- `docs/design-docs/architecture-decisions.md`: 확정된 설계 결정
- `docs/design-docs/layered-architecture.md`: 계층과 모듈 의존 방향
- `docs/code-conventions.md`: Java/Kotlin 코드 작성 기준
- `docs/design-docs/storage-design.md`: RDB/Object Storage/search index 저장 정책
- `docs/product-specs/api-contract.md`: API 요청/응답과 상태 전이
- `docs/testing-strategy.md`: 테스트 종류와 모듈별 배치
- `docs/design-docs/benchmark-plan.md`: 성능 측정 방식과 리포트 기준
- `docs/operations-runbook.md`: 로컬/AWS 실행과 복구 절차
- `docs/exec-plans/index.md`: 사용자가 순차적으로 구현할 phase별 작업 목록
- 이후 구현이 시작되면 `docs/benchmark-result.md`: 성능 측정 결과

## 향후 권장 모듈

```text
mini-search-engine
├─ search-api
│  └─ Kotlin + Spring Boot
├─ search-core
│  └─ Java 21, Spring 의존성 없음
├─ search-storage
│  └─ ObjectStorage abstraction, MinIO/S3 adapter
├─ search-benchmark
│  └─ Java 21 benchmark runner
└─ docs
```

## 핵심 데이터 흐름

```text
파일 업로드
→ Object Storage에 원본 파일 저장
→ RDB에 document metadata 저장
→ 텍스트 추출
→ Object Storage에 extracted text 저장
→ search-core에 SearchDocument 전달
→ in-memory index 또는 file segment에 색인
→ 검색 시 docId, score 반환
→ API가 RDB/Object Storage와 조합해 응답
```

## 첫 구현 범위 기본값

- 업로드 파일 타입: `txt`, `md`
- 검색 core: in-memory index부터 시작
- Analyzer: 확장 가능한 구조, 기본 동작은 lowercase + 문자/숫자 토큰화
- Ranking: BM25
- Top-K: PriorityQueue 기반
- Segment, Tombstone, Merge: 후속 단계
- Benchmark: custom runner부터 시작하고, JMH는 microbenchmark가 필요할 때 추가

## Codex가 작업할 때 지켜야 할 것

- 구현 전 이 문서와 `AGENTS.md`를 확인한다.
- 계층/의존성 판단이 필요하면 `docs/design-docs/layered-architecture.md`를 우선한다.
- 코드 스타일 판단이 필요하면 `docs/code-conventions.md`를 우선한다.
- 저장소, API, 테스트, 성능, 운영 절차 판단은 각 전용 문서를 우선한다.
- 사용자의 포트폴리오 메시지와 맞지 않는 범위 확장은 먼저 짧게 근거를 설명한다.
- 검색 엔진 핵심 구현을 흐리게 만드는 라이브러리 도입을 피한다.
- 저장소 경계는 `Object Storage/RDB/search index`로 유지한다.
- 테스트나 벤치마크 결과를 만들었다면 어떤 명령으로 검증했는지 남긴다.

## 남아 있는 열린 질문

- PDF/Word 추출을 언제 넣을지
- AWS 배포 시 검색 index directory를 EBS, EFS, 또는 ephemeral + rebuild 중 무엇으로 둘지
- PostgreSQL 스키마를 JPA로 관리할지, Flyway/Liquibase로 관리할지
- file-based segment format을 텍스트/JSON으로 시작할지, binary format으로 바로 갈지
