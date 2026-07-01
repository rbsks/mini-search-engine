# Design Docs Index

설계 문서는 구현자가 판단을 내려야 할 때 우선 참고하는 문서다.

## Core Docs

- `architecture-decisions.md`: 이미 확정한 의사결정과 변경 이유
- `layered-architecture.md`: 모듈/계층 의존 방향과 금지 의존성
- `storage-design.md`: Object Storage, RDB, search index 책임 분리
- `benchmark-plan.md`: 성능 측정 목표, 지표, 비교 실험 기준

## Rules

- 결정을 바꾸면 `architecture-decisions.md`에 새 항목으로 남긴다.
- 구현 중 의존성 방향이 애매하면 `layered-architecture.md`를 우선한다.
- 저장 위치나 복구 전략이 애매하면 `storage-design.md`를 우선한다.
