package com.minisearchengine.core.index;

/**
 * 특정 term이 특정 문서에 등장했다는 사실을 나타내는 값 객체다.
 *
 * <p>{@code documentId}는 term이 등장한 문서를 가리키고, {@code termFrequency}는 그 문서 안에서
 * 해당 term이 몇 번 등장했는지를 의미한다. 예를 들어 "search"가 1번 문서에 두 번 등장하면
 * {@code Posting(1, 2)}로 표현된다.
 */
public record Posting(long documentId, int termFrequency) {
}
