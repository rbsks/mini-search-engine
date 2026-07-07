package com.minisearchengine.core.index;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryInvertedIndexTest {


    @Test
    @DisplayName("문서 토큰을 추가하면 term별 posting list를 조회할 수 있다")
    void returnsPostingListByTermWhenDocumentTokensAreAdded() {
        InMemoryInvertedIndex inMemoryInvertedIndex = new InMemoryInvertedIndex();

        long documentId = 1L;
        List<String> tokens = List.of("mini", "search", "ai", "doc", "123", "검색", "engine");

        inMemoryInvertedIndex.add(documentId, tokens);

        for (String token : tokens) {
            List<Posting> postings = inMemoryInvertedIndex.postings(token);
            assertEquals(1, postings.size());
            assertEquals(documentId, postings.getFirst().documentId());
        }
    }

    @Test
    @DisplayName("같은 문서 안의 반복 토큰은 termFrequency로 집계된다")
    void countsRepeatedTokensAsTermFrequencyWithinSameDocument() {
        InMemoryInvertedIndex inMemoryInvertedIndex = new InMemoryInvertedIndex();

        long documentId = 1L;
        List<String> tokens = List.of("search", "search");

        inMemoryInvertedIndex.add(documentId, tokens);

        List<Posting> postings = inMemoryInvertedIndex.postings("search");
        assertEquals(1, postings.size());
        assertEquals(documentId, postings.getFirst().documentId());
        assertEquals(2, postings.getFirst().termFrequency());
    }

    @Test
    @DisplayName("documentFrequency는 term이 등장한 문서 수로 계산된다")
    void countsDocumentFrequencyByDocumentsContainingTerm() {
        InMemoryInvertedIndex inMemoryInvertedIndex = new InMemoryInvertedIndex();

        long documentId = 1L;
        List<String> tokens = List.of("search", "search");
        inMemoryInvertedIndex.add(documentId, tokens);

        documentId = 2L;
        tokens = List.of("search", "search");
        inMemoryInvertedIndex.add(documentId, tokens);

        assertEquals(2, inMemoryInvertedIndex.documentFrequency("search"));
    }

    @Test
    @DisplayName("존재하지 않는 term은 빈 posting list를 반환한다")
    void returnsEmptyPostingListForUnknownTerm() {
        InMemoryInvertedIndex inMemoryInvertedIndex = new InMemoryInvertedIndex();

        long documentId = 1L;
        List<String> tokens = List.of("mini", "search", "ai", "doc", "123", "검색", "engine");

        inMemoryInvertedIndex.add(documentId, tokens);

        List<Posting> postings = inMemoryInvertedIndex.postings("test");
        assertEquals(0, postings.size());
    }

    @Test
    @DisplayName("문서별 token length를 저장한다")
    void storesTokenLengthByDocumentId() {
        InMemoryInvertedIndex inMemoryInvertedIndex = new InMemoryInvertedIndex();

        inMemoryInvertedIndex.add(1L, List.of("mini", "search", "engine"));
        inMemoryInvertedIndex.add(2L, List.of());

        assertEquals(3, inMemoryInvertedIndex.documentLength(1L));
        assertEquals(0, inMemoryInvertedIndex.documentLength(2L));
    }

    @Test
    @DisplayName("전체 document count를 계산한다")
    void countsIndexedDocuments() {
        InMemoryInvertedIndex inMemoryInvertedIndex = new InMemoryInvertedIndex();

        inMemoryInvertedIndex.add(1L, List.of("mini"));
        inMemoryInvertedIndex.add(2L, List.of("search", "engine"));
        inMemoryInvertedIndex.add(3L, List.of());

        assertEquals(3, inMemoryInvertedIndex.documentCount());
    }

    @Test
    @DisplayName("평균 문서 길이를 계산한다")
    void calculatesAverageDocumentLength() {
        InMemoryInvertedIndex inMemoryInvertedIndex = new InMemoryInvertedIndex();

        inMemoryInvertedIndex.add(1L, List.of("mini", "search", "engine"));
        inMemoryInvertedIndex.add(2L, List.of("core"));
        inMemoryInvertedIndex.add(3L, List.of());

        assertEquals(4.0 / 3.0, inMemoryInvertedIndex.averageDocumentLength(), 0.000001);
    }

    @Test
    @DisplayName("존재하지 않는 term의 documentFrequency는 0이다")
    void returnsZeroDocumentFrequencyForUnknownTerm() {
        InMemoryInvertedIndex inMemoryInvertedIndex = new InMemoryInvertedIndex();

        inMemoryInvertedIndex.add(1L, List.of("mini", "search"));

        assertEquals(0, inMemoryInvertedIndex.documentFrequency("unknown"));
    }

    @Test
    @DisplayName("빈 token list 문서도 document count에 포함된다")
    void countsDocumentWithEmptyTokenList() {
        InMemoryInvertedIndex inMemoryInvertedIndex = new InMemoryInvertedIndex();

        inMemoryInvertedIndex.add(1L, List.of());

        assertEquals(1, inMemoryInvertedIndex.documentCount());
        assertEquals(0, inMemoryInvertedIndex.documentLength(1L));
    }

    @Test
    @DisplayName("중복 documentId 색인은 거부한다")
    void rejectsDuplicateDocumentId() {
        InMemoryInvertedIndex inMemoryInvertedIndex = new InMemoryInvertedIndex();

        inMemoryInvertedIndex.add(1L, List.of("mini"));

        assertThrows(IllegalArgumentException.class, () -> inMemoryInvertedIndex.add(1L, List.of("search")));
    }

    @Test
    @DisplayName("documentId가 양수가 아니면 색인할 수 없다")
    void rejectsNonPositiveDocumentId() {
        InMemoryInvertedIndex inMemoryInvertedIndex = new InMemoryInvertedIndex();

        assertThrows(IllegalArgumentException.class, () -> inMemoryInvertedIndex.add(0L, List.of("mini")));
        assertThrows(IllegalArgumentException.class, () -> inMemoryInvertedIndex.add(-1L, List.of("mini")));
        assertEquals(0, inMemoryInvertedIndex.documentCount());
    }

    @Test
    @DisplayName("존재하지 않는 documentId의 문서 길이는 조회할 수 없다")
    void rejectsDocumentLengthLookupForUnknownDocumentId() {
        InMemoryInvertedIndex inMemoryInvertedIndex = new InMemoryInvertedIndex();

        inMemoryInvertedIndex.add(1L, List.of());

        assertThrows(IllegalArgumentException.class, () -> inMemoryInvertedIndex.documentLength(2L));
    }
}
