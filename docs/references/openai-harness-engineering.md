# OpenAI Harness Engineering Notes

Source: https://openai.com/ko-KR/index/harness-engineering/

이 프로젝트에 반영할 점:

- `AGENTS.md`는 긴 백과사전이 아니라 짧은 지도 역할을 한다.
- 자세한 지식은 `docs/` 아래에 분류하고 색인한다.
- 실행 계획은 일회성 채팅이 아니라 repo 안의 버전 관리되는 아티팩트로 둔다.
- 진행 중인 계획, 완료된 계획, 기술 부채를 분리해 관리한다.
- 아키텍처 의존 방향과 취향은 문서에만 두지 말고 향후 구조 테스트나 lint로 강제한다.
- 에이전트가 읽고 검증할 수 있는 형태의 로그, 테스트, benchmark report를 늘린다.
