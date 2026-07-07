package com.minisearchengine.core.index;

import com.minisearchengine.core.index.port.InvertedIndex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Phase 01에서 사용하는 메모리 기반 역색인 구현체다.
 *
 * <p>이 클래스는 검색 core의 첫 MVP를 위한 단순한 객체 기반 구현이다. term별 posting list는
 * {@code postingLists}에 저장하고, BM25 계산에 필요한 문서 길이 통계는 {@code documentLengths}에 분리해 저장한다.
 * 이렇게 나누면 "term이 어떤 문서에 등장했는가"와 "문서 자체의 통계는 무엇인가"가 섞이지 않는다.
 *
 * <p>Analyzer를 직접 호출하지 않고 이미 분석된 token list만 받는다. 따라서 analyzer 정책이 바뀌어도
 * 이 클래스는 term/posting/document statistics 관리 책임에만 집중할 수 있다.
 */
public class InMemoryInvertedIndex implements InvertedIndex {

    private final Map<String, PostingList> postingLists = new HashMap<>();
    private final Map<Long, Integer> documentLengths = new HashMap<>();

    /**
     * 문서 하나를 메모리 색인에 추가한다.
     *
     * <p>먼저 documentId 정책을 검증한 뒤 문서 길이를 저장하고, 각 token을 term으로 보아
     * term별 posting list에 documentId를 추가한다. 같은 문서 안에서 같은 token이 반복되면
     * {@link PostingList#add(long)}가 해당 documentId의 term frequency를 증가시킨다.
     */
    @Override
    public void add(long documentId, List<String> tokens) {
        if (documentId <= 0) {
            throw new IllegalArgumentException("documentId는 양수여야 합니다.");
        }

        if (documentLengths.containsKey(documentId)) {
            // TODO-INVERTED-INDEX 추후 가능하도록 개선해야 함
            throw new IllegalArgumentException("동일한 documentId는 재색인이 불가능 합니다.");
        }

        documentLengths.put(documentId, tokens.size());

        for (String token : tokens) {
            postingLists
                    .computeIfAbsent(token, ignored -> new PostingList())
                    .add(documentId);
        }
    }

    /**
     * term에 연결된 posting 목록을 조회한다.
     *
     * <p>없는 term은 검색 후보가 없다는 뜻이므로 빈 목록을 반환한다. 실제 목록 정렬과 불변 반환 정책은
     * {@link PostingList#postings()}에 위임한다.
     */
    @Override
    public List<Posting> postings(String term) {
        return postingLists.get(term) == null ? List.of() : postingLists.get(term).postings();
    }

    /**
     * term이 등장한 문서 수를 조회한다.
     *
     * <p>posting list가 없으면 해당 term이 색인에 없다는 뜻이므로 0을 반환한다. 같은 문서 안의 반복 횟수는
     * document frequency가 아니라 posting의 term frequency에서 다룬다.
     */
    @Override
    public int documentFrequency(String term) {
        return postingLists.get(term) == null ? 0 : postingLists.get(term).documentFrequency();
    }

    /**
     * 특정 문서의 token length를 조회한다.
     *
     * <p>빈 문서는 길이 0인 정상 문서일 수 있다. 그래서 존재하지 않는 documentId도 0으로 반환하면
     * "빈 문서"와 "색인되지 않은 문서"가 구분되지 않는다. 없는 문서는 호출 쪽 버그일 가능성이 높으므로 예외로 막는다.
     */
    @Override
    public int documentLength(long documentId) {
        if (!documentLengths.containsKey(documentId)) {
            throw new IllegalArgumentException("색인되지 않은 documentId입니다.");
        }
        return documentLengths.get(documentId);
    }

    /**
     * 색인된 문서 수를 반환한다.
     *
     * <p>{@code documentLengths}는 documentId별로 하나의 entry만 가지므로, 이 map의 크기가 전체 문서 수다.
     * token list가 비어 있는 문서도 {@link #add(long, List)}에서 길이 0으로 저장되므로 count에 포함된다.
     */
    @Override
    public int documentCount() {
        return documentLengths.size();
    }

    /**
     * 색인된 문서들의 평균 token length를 계산한다.
     *
     * <p>현재는 문서 수가 작다는 전제로 매번 평균을 계산한다. 나중에 benchmark에서 병목이 확인되면
     * 총 token 수를 별도 필드로 유지해 O(1)로 바꾸는 식의 최적화를 검토할 수 있다.
     */
    @Override
    public double averageDocumentLength() {
        return documentLengths.values().stream()
                .mapToInt(v -> v)
                .average()
                .orElse(0);
    }
}
