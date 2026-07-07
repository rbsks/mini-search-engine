package com.minisearchengine.core.index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 하나의 term에 대한 posting 목록을 관리하는 객체다.
 *
 * <p>PostingList는 자신이 어떤 term에 연결되어 있는지 알지 않는다. term 문자열과 PostingList의 매핑은
 * {@link InMemoryInvertedIndex}가 담당한다. 이 클래스는 "이미 특정 term에 대한 목록"이라는 전제 아래에서,
 * documentId별 term frequency만 관리한다.
 */
public class PostingList {

    private final Map<Long, Integer> termFrequencies = new HashMap<>();

    /**
     * 이 posting list에 documentId를 한 번 등장한 것으로 추가한다.
     *
     * <p>같은 documentId가 여러 번 추가되면 같은 문서 안에서 같은 term이 반복 등장한 것이므로
     * term frequency를 1씩 증가시킨다. documentId는 색인 전체에서 문서를 식별하는 값이므로 양수만 허용한다.
     */
    public void add(long documentId) {
        if (documentId <= 0) {
            throw new IllegalArgumentException("documentId는 양수여야 합니다.");
        }
        termFrequencies.merge(documentId, 1, Integer::sum);
    }

    /**
     * 현재 posting list를 documentId 오름차순으로 반환한다.
     *
     * <p>내부 map을 그대로 노출하지 않고 {@link Posting} 값 객체 목록으로 변환한다. 정렬을 고정해두면
     * 테스트와 검색 결과 후보 수집 과정이 HashMap 순회 순서에 흔들리지 않는다.
     */
    public List<Posting> postings() {
        return termFrequencies.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new Posting(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * 이 term이 등장한 문서 수를 반환한다.
     *
     * <p>document frequency는 term frequency의 합계가 아니다. 같은 문서 안에서 term이 여러 번 등장해도
     * document frequency에는 문서 하나로만 집계된다.
     */
    public int documentFrequency() {
        return termFrequencies.size();
    }
}
