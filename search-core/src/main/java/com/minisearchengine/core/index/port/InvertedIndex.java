package com.minisearchengine.core.index.port;

import com.minisearchengine.core.index.Posting;

import java.util.List;

/**
 * Analyzer가 만든 토큰 목록을 검색 가능한 역색인 구조로 저장하고 조회하는 계약이다.
 *
 * <p>이 인터페이스는 원문 문자열을 직접 분석하지 않는다. 원문 분석은 analyzer 계층의 책임이고,
 * 이 계층은 이미 정규화된 token list를 받아 term별 posting list와 문서 통계를 관리한다.
 * 이후 BM25 ranking 단계에서는 이 인터페이스를 통해 document count, document length,
 * document frequency, posting의 term frequency를 조회하게 된다.
 */
public interface InvertedIndex {

    /**
     * 하나의 문서를 색인에 추가한다.
     *
     * <p>{@code tokens}는 Analyzer를 통과한 결과여야 한다. 같은 문서 안에 동일한 token이 여러 번 들어오면
     * 해당 term의 posting에서 term frequency가 증가해야 한다. 동일한 {@code documentId}를 다시 색인할지
     * 거부할지는 구현체의 정책으로 정하되, Phase 01에서는 재색인을 거부한다.
     */
    void add(long documentId, List<String> tokens);

    /**
     * 특정 term이 등장한 문서들의 posting list를 반환한다.
     *
     * <p>반환되는 {@link Posting}은 documentId와 해당 문서 안에서의 term frequency를 가진다.
     * 존재하지 않는 term은 예외보다 빈 목록을 반환하는 편이 검색 후보 수집 단계에서 다루기 쉽다.
     */
    List<Posting> postings(String term);

    /**
     * 특정 term이 등장한 문서 수를 반환한다.
     *
     * <p>document frequency는 등장 횟수의 합이 아니라 "몇 개의 문서에 등장했는가"다.
     * 예를 들어 한 문서에 같은 term이 3번 등장해도 document frequency에는 1만 더해진다.
     */
    int documentFrequency(String term);

    /**
     * 특정 문서의 길이를 반환한다.
     *
     * <p>Phase 01에서 문서 길이는 원문 문자열 길이가 아니라 Analyzer가 만든 token 개수다.
     * 이 값은 BM25의 {@code dl} 계산에 사용된다.
     */
    int documentLength(long documentId);

    /**
     * 현재 색인에 등록된 전체 문서 수를 반환한다.
     *
     * <p>BM25에서 전체 문서 수 {@code N}에 해당한다. token이 하나도 없는 빈 문서도 색인에 추가됐다면
     * document count에 포함되어야 한다.
     */
    int documentCount();

    /**
     * 색인된 문서들의 평균 문서 길이를 반환한다.
     *
     * <p>각 문서의 token 개수를 평균낸 값이며, BM25에서 {@code avgdl}로 사용된다.
     * 문서가 하나도 없는 경우에는 구현체가 0을 반환해도 된다.
     */
    double averageDocumentLength();
}
