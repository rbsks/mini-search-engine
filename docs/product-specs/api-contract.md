# API Contract

이 문서는 초기 HTTP API의 요청/응답, 상태 코드, 상태 전이를 정한다.

## Document Upload

```http
POST /documents
Content-Type: multipart/form-data
```

요청 필드:

```text
file: 업로드 파일
title: 선택
tags: 선택, comma-separated 또는 반복 필드
```

초기 지원 파일:

```text
txt
md
```

응답:

```json
{
  "documentId": 1,
  "originalFileName": "jpa-note.md",
  "indexStatus": "INDEXED"
}
```

상태 코드:

```text
201 Created: 업로드와 색인 성공
400 Bad Request: 파일 누락, 지원하지 않는 타입, 빈 파일
413 Payload Too Large: 허용 크기 초과
500 Internal Server Error: 예상하지 못한 서버 오류
```

## Search

```http
GET /search?q=spring%20jpa&topK=20
```

기본값:

```text
topK=20
max topK=100
```

응답:

```json
{
  "query": "spring jpa",
  "totalHits": 2,
  "tookMs": 8,
  "results": [
    {
      "documentId": 1,
      "title": "JPA N+1 문제 해결",
      "score": 12.41
    }
  ]
}
```

상태 코드:

```text
200 OK: 검색 성공
400 Bad Request: 빈 query, topK 범위 초과
```

정렬:

```text
score desc
documentId asc for ties
```

## Document Delete

```http
DELETE /documents/{documentId}
```

초기 구현:

- RDB metadata 상태를 삭제 또는 삭제 예정 상태로 변경한다.
- search-core in-memory 단계에서는 즉시 검색 결과에서 제외한다.
- segment 단계에서는 tombstone으로 처리하고 merge 시 물리 제거한다.

상태 코드:

```text
204 No Content: 삭제 처리 성공
404 Not Found: 문서 없음
```

## Document Status

```http
GET /documents/{documentId}
```

응답:

```json
{
  "documentId": 1,
  "originalFileName": "jpa-note.md",
  "contentType": "text/markdown",
  "size": 1204,
  "indexStatus": "INDEXED",
  "createdAt": "2026-07-01T21:00:00+09:00"
}
```

## Index Status 전이

```text
UPLOADED
→ TEXT_EXTRACTED
→ INDEXED

UPLOADED/TEXT_EXTRACTED
→ FAILED
```

규칙:

- 실패 응답에 내부 stack trace를 노출하지 않는다.
- 클라이언트 응답 DTO에 storage key를 기본 노출하지 않는다.
- API 계층은 search-core 내부 타입을 그대로 노출하지 않는다.
