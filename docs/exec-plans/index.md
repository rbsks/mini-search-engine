# Execution Plans Index

이 디렉터리는 사용자가 직접 구현하고 Codex가 리뷰하는 페어 프로그래밍용 실행 계획을 관리한다.

## Active Plans

1. `active/phase-00-project-skeleton.md`
2. `active/phase-01-search-core-mvp.md`
3. `active/phase-02-api-storage-skeleton.md`
4. `active/phase-03-persistence-e2e.md`
5. `active/phase-04-benchmarking.md`
6. `active/phase-05-segment-storage.md`

## Completed Plans

- 완료된 phase는 `completed/`로 옮기고, 실제 구현 중 바뀐 결정과 검증 명령을 남긴다.

## Tech Debt

- `tech-debt-tracker.md`에 나중에 정리할 설계/테스트/문서 부채를 기록한다.

## Workflow

- 사용자는 phase 파일을 위에서부터 순서대로 구현한다.
- 각 phase의 review checkpoint에서 Codex에게 리뷰를 요청한다.
- Codex는 구현을 대신하기보다 코드 리뷰, 설계 점검, 다음 단계 정리를 우선한다.
