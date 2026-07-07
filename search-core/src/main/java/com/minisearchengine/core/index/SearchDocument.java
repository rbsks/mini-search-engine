package com.minisearchengine.core.index;

/**
 * search-core에 색인할 문서를 표현하는 최소 입력 모델이다.
 *
 * <p>이 객체는 API 계층의 요청 DTO나 RDB 엔티티가 아니다. 검색 core가 알아야 하는 값만 담는다.
 * 이후 {@code SearchEngine.index(...)}가 이 객체를 받아 {@code content}를 Analyzer로 토큰화하고,
 * 만들어진 token list를 {@link InMemoryInvertedIndex}에 전달하는 흐름으로 사용된다.
 */
public record SearchDocument(long documentId, String content) {
}
