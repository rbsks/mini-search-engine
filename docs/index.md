# Documentation Index

이 문서는 리포지터리 지식의 진입점이다. `AGENTS.md`는 짧은 지도 역할만 하고, 자세한 정보는 이 문서에서 연결된 전용 문서를 따른다.

## Start Here

- `../AGENTS.md`: 에이전트 작업 규칙과 읽기 순서
- `codex-harness.md`: 현재 합의된 프로젝트 컨텍스트
- `mini-search-engine-portfolio-plan.md`: 프로젝트 원안과 장기 방향. 필요할 때만 참조한다.

## Task Routing

- 모듈/계층/의존성 변경:
  - `design-docs/layered-architecture.md`
  - `design-docs/architecture-decisions.md`

- search-core 구현:
  - `design-docs/layered-architecture.md`
  - `exec-plans/active/phase-01-search-core-mvp.md`
  - `code-conventions.md`

- 업로드/스토리지/RDB 작업:
  - `design-docs/storage-design.md`
  - `product-specs/api-contract.md`
  - `exec-plans/active/phase-02-api-storage-skeleton.md`
  - `exec-plans/active/phase-03-persistence-e2e.md`

- API 작업:
  - `product-specs/api-contract.md`
  - `design-docs/layered-architecture.md`
  - `code-conventions.md`

- 테스트 작업:
  - `testing-strategy.md`
  - 관련 phase 파일

- 벤치마크 작업:
  - `design-docs/benchmark-plan.md`
  - `exec-plans/active/phase-04-benchmarking.md`

- segment/file index 작업:
  - `design-docs/storage-design.md`
  - `exec-plans/active/phase-05-segment-storage.md`

- 로컬 실행/AWS/복구 작업:
  - `operations-runbook.md`
  - `design-docs/storage-design.md`

- 프로젝트 배경이나 포트폴리오 메시지 확인:
  - `mini-search-engine-portfolio-plan.md`

## Design Docs

- `design-docs/index.md`: 설계 문서 지도
- `design-docs/architecture-decisions.md`: 결정 기록
- `design-docs/layered-architecture.md`: 모듈/계층 의존 방향
- `design-docs/storage-design.md`: RDB/Object Storage/search index 저장 정책
- `design-docs/benchmark-plan.md`: 성능 측정 계획

## Product Specs

- `product-specs/index.md`: 제품/API 명세 지도
- `product-specs/api-contract.md`: 초기 API 계약

## Execution Plans

- `exec-plans/index.md`: phase별 구현 계획 지도
- `exec-plans/active/`: 진행 중이거나 예정된 phase 계획
- `exec-plans/completed/`: 완료된 phase 기록
- `exec-plans/tech-debt-tracker.md`: 알려진 기술 부채와 정리 후보

## Engineering Rules

- `code-conventions.md`: Java/Kotlin 코드 컨벤션
- `testing-strategy.md`: 테스트 전략
- `operations-runbook.md`: 로컬/AWS 실행과 복구 절차

## References And Generated

- `references/index.md`: 외부 참고자료와 요약
- `generated/README.md`: 코드나 도구로 생성한 문서 보관 위치
