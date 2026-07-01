# Architecture Decisions

이 문서는 대화 중 확정한 결정을 기록한다. 결정을 바꾸면 새 항목을 추가하고 기존 결정의 변경 이유를 남긴다.

## 1. Lucene-inspired core

- 결정: Lucene을 의존성으로 쓰지 않고, Lucene과 유사한 segment 기반 search core를 직접 구현한다.
- 이유: 포트폴리오의 핵심은 검색 엔진 사용 경험이 아니라 역색인, posting list, BM25, segment, merge 구조를 직접 설명하고 측정하는 것이다.
- 결과: `search-core`는 라이브러리처럼 API 서버 내부에서 호출된다.

## 2. Source of truth와 search index 분리

- 결정: RDB/Object Storage를 source of truth로 보고, search index files는 재생성 가능한 파생 데이터로 둔다.
- 이유: 검색 색인은 성능을 위한 구조이며 장애나 재배포 상황에서 원본 데이터로 rebuild할 수 있어야 한다.
- 결과: 색인 파일이 유실되어도 RDB metadata와 Object Storage의 extracted text로 복구할 수 있는 방향으로 설계한다.

## 3. 원본 파일과 추출 텍스트 저장소

- 결정: 원본 파일과 추출 텍스트는 RDB가 아니라 Object Storage에 저장한다.
- 이유: 추출 텍스트와 업로드 파일은 커질 수 있고, RDB에 큰 payload를 직접 저장하면 백업, replication, cache 효율, 조회 실수 비용이 커진다.
- 결과: RDB에는 `original_storage_key`, `extracted_text_storage_key`, 상태와 메타데이터만 저장한다.

## 4. MinIO와 S3 사용 위치

- 결정: 로컬 개발과 통합 테스트는 MinIO, AWS 테스트/운영 유사 환경은 S3를 사용한다.
- 이유: MinIO는 S3-compatible API를 로컬에서 검증하기 좋고, AWS에서는 managed service인 S3가 운영 부담이 낮다.
- 결과: 코드는 `ObjectStorage` 인터페이스를 사용하고 profile/config로 MinIO와 S3 구현을 교체한다.

## 5. 언어와 모듈

- 결정: 검색 core는 Java 21, API는 기존 Kotlin/Spring Boot를 유지한다.
- 이유: Java 21은 Lucene과의 설명 연결, JVM 객체/GC/primitive 구조 최적화 스토리를 보여주기 좋다. 기존 Kotlin API는 현재 프로젝트 시작점을 살릴 수 있다.
- 결과: Gradle 멀티모듈로 `search-core`, `search-api`, `search-storage`, `search-benchmark`를 분리하는 방향으로 간다.

## 6. 첫 구현 파일 타입

- 결정: 첫 업로드/추출 대상은 `txt`, `md`로 시작한다.
- 이유: PDF/Word 파싱은 별도 복잡도가 크고, 초기 목표인 검색 core와 저장소 경계 검증을 흐릴 수 있다.
- 결과: PDF/Word는 하네스와 core 흐름이 안정화된 뒤 확장한다.
