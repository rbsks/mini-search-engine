# Mini Search Engine Agent Guide

이 저장소에서 작업하는 에이전트는 아래 기준을 우선 참고한다.

## 프로젝트 목표

- Elasticsearch/Lucene을 직접 사용하지 않고, 단일 노드 문서 검색 엔진의 핵심 구조를 직접 구현한다.
- 핵심 범위는 문서 업로드, 텍스트 추출, 역색인, Posting List, BM25, Top-K, Segment, Tombstone, Segment Merge, 성능 측정이다.
- Lucene은 구현 의존성이 아니라 설계 참고 대상이다.

## 아키텍처 기본 결정

- 구조는 모듈러 모놀리식으로 간다.
- `search-core`는 Java 21 기반 순수 Java 모듈로 둔다. Spring, RDB, Object Storage에 의존하지 않는다.
- `search-api`는 기존 Kotlin/Spring Boot 진입점을 살려 API 서버 역할을 맡는다.
- `search-storage`는 S3-compatible Object Storage 접근을 추상화한다.
- `search-benchmark`는 성능 측정 러너를 둔다.
- 원본 업로드 파일과 추출 텍스트는 Object Storage에 저장한다.
- 로컬 개발은 MinIO, AWS 테스트/운영 유사 환경은 S3를 사용한다.
- RDB는 문서 메타데이터와 처리 상태의 source of truth다.
- 검색 색인 파일은 `search-core`가 관리하는 재생성 가능한 파생 데이터다.

## 저장소 책임 분리

- Object Storage: 원본 파일, 추출 텍스트
- RDB: 파일명, storage key, content type, size, checksum, index status, created/updated time
- Search index files: term dictionary, posting list, doc length, BM25 통계, segment metadata

## 구현 원칙

- 검색 core에는 Elasticsearch, Lucene 같은 완성형 검색 엔진 의존성을 추가하지 않는다.
- 첫 파일 업로드 대상은 `txt`, `md`처럼 텍스트 추출이 단순한 형식부터 시작한다. PDF/Word는 후속 확장으로 둔다.
- BM25 기본값은 `k1=1.2`, `b=0.75`로 시작한다.
- 검색 결과 정렬은 `score desc`, 동점 시 `documentId asc`로 결정적으로 만든다.
- 하드코딩된 성능 주장을 남기지 말고, 측정 결과는 `docs/benchmark-result.md` 또는 benchmark report에 기록한다.
- 새 기능은 가능하면 작은 고정 코퍼스 테스트와 함께 추가한다.

## 작업 전 참고 순서

1. `docs/index.md`
2. `docs/codex-harness.md`
3. 필요할 때만 `docs/mini-search-engine-portfolio-plan.md`

`docs/mini-search-engine-portfolio-plan.md`는 원본 기획/배경 reference다. 매 작업마다 전체를 읽지 말고, 프로젝트 의도나 장기 범위가 불명확할 때만 참조한다.

## 로컬 빌드 주의

현재 기본 `JAVA_HOME`이 JDK 11일 수 있다. Gradle 실행 전 JDK 21을 명시한다.

```powershell
$env:JAVA_HOME=$env:JAVA_HOME_21
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat test
```
