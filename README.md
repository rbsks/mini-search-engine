# Mini Search Engine

문서 업로드, 텍스트 추출, 역색인, BM25 랭킹, Top-K 검색, file-based segment를 직접 구현하는 단일 노드 문서 검색 엔진 프로젝트입니다.

이 프로젝트는 Elasticsearch나 Lucene을 사용하는 대신, 검색 엔진 내부에서 일어나는 핵심 흐름을 직접 구현하고 측정하는 것을 목표로 합니다.

```text
문서 업로드
→ 원본 파일 저장
→ 텍스트 추출
→ 역색인 생성
→ BM25 점수 계산
→ Top-K 검색
→ 성능 측정
```

## Goals

- 문서를 토큰화하고 `term -> posting list` 형태의 역색인을 직접 구현합니다.
- TF, IDF, 문서 길이 정규화, term frequency saturation을 반영한 BM25 랭킹을 구현합니다.
- 검색 결과 전체 정렬 대신 PriorityQueue 기반 Top-K 수집을 적용합니다.
- in-memory index에서 시작해 file-based segment, tombstone, segment merge로 확장합니다.
- 색인 처리량, p50/p95/p99 latency, heap 사용량, GC 지표를 측정합니다.
- 원본 데이터와 검색 색인을 분리해, 검색 색인을 재생성 가능한 파생 데이터로 관리합니다.

## Architecture

이 프로젝트는 모듈러 모놀리식 구조로 구성합니다.

```text
mini-search-engine
├─ search-api
│  └─ Kotlin + Spring Boot API server
├─ search-core
│  └─ Java 21 search engine core
├─ search-storage
│  └─ S3-compatible object storage abstraction
├─ search-benchmark
│  └─ benchmark runner
└─ docs
```

### Module Responsibilities

| Module | Responsibility |
| --- | --- |
| `search-api` | HTTP API, upload flow, transaction boundary, application orchestration |
| `search-core` | Analyzer, inverted index, posting list, BM25, Top-K, segment search |
| `search-storage` | MinIO/S3 compatible object storage abstraction |
| `search-benchmark` | indexing/search benchmark runner and report generation |

`search-core`는 Spring, RDB, Object Storage에 의존하지 않는 순수 Java 모듈입니다.

## Storage Model

```text
Object Storage
├─ original uploaded file
└─ extracted text

RDB
└─ document metadata and indexing status

search-core index directory
└─ derived search index files
```

로컬 개발에서는 MinIO를 사용하고, AWS 환경에서는 S3를 사용합니다.

검색 색인은 source of truth가 아니라 재생성 가능한 파생 데이터입니다. 색인 파일이 유실되더라도 RDB metadata와 Object Storage의 extracted text를 기반으로 rebuild할 수 있는 구조를 지향합니다.

## Tech Stack

- Java 21
- Kotlin 2.1.21
- Spring Boot 3.4.13
- Gradle 8.12.1
- PostgreSQL
- MinIO / AWS S3
- JUnit 5

## Current Status

현재는 프로젝트 하네스와 멀티모듈 골격을 구성한 초기 단계입니다.

- [x] Gradle 멀티모듈 구성
- [x] `search-api`, `search-core`, `search-storage`, `search-benchmark` 모듈 분리
- [x] Codex 작업용 문서 하네스 구성
- [ ] `search-core` in-memory search engine MVP
- [ ] upload/search API skeleton
- [ ] Object Storage abstraction
- [ ] PostgreSQL metadata persistence
- [ ] benchmark runner
- [ ] file-based segment storage

## Getting Started

JDK 21을 사용합니다.

Windows PowerShell:

```powershell
$env:JAVA_HOME=$env:JAVA_HOME_21
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat projects
.\gradlew.bat test
```

Gradle modules 확인:

```powershell
.\gradlew.bat projects
```

전체 테스트:

```powershell
.\gradlew.bat test
```

API 서버 실행:

```powershell
.\gradlew.bat :search-api:bootRun
```

## Implementation Plan

구현은 phase 단위로 진행합니다.

| Phase | Plan |
| --- | --- |
| Phase 00 | Project skeleton 확인 |
| Phase 01 | `search-core` Analyzer, InvertedIndex, BM25, Top-K |
| Phase 02 | API skeleton, ObjectStorage abstraction |
| Phase 03 | PostgreSQL metadata, upload-to-search E2E |
| Phase 04 | Benchmark runner |
| Phase 05 | File-based segment storage |

자세한 작업 목록은 [docs/exec-plans/index.md](docs/exec-plans/index.md)를 참고합니다.

## Documentation

이 프로젝트는 에이전트와 사람이 같은 맥락에서 작업할 수 있도록 문서 하네스를 둡니다.

- [docs/index.md](docs/index.md): 문서 지도
- [docs/codex-harness.md](docs/codex-harness.md): 작업 컨텍스트 요약
- [docs/design-docs/layered-architecture.md](docs/design-docs/layered-architecture.md): 계층과 의존 방향
- [docs/design-docs/storage-design.md](docs/design-docs/storage-design.md): 저장소 설계
- [docs/product-specs/api-contract.md](docs/product-specs/api-contract.md): API 계약
- [docs/testing-strategy.md](docs/testing-strategy.md): 테스트 전략
- [docs/design-docs/benchmark-plan.md](docs/design-docs/benchmark-plan.md): 벤치마크 계획