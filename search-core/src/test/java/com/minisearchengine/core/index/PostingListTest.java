package com.minisearchengine.core.index;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostingListTest {

    @Test
    @DisplayName("같은 documentId를 여러 번 추가하면 하나의 posting에서 termFrequency가 증가한다")
    void increasesTermFrequencyWhenSameDocumentIdIsAddedRepeatedly() {
        long documentId = 1L;
        PostingList postingList = new PostingList();
        postingList.add(documentId);
        postingList.add(documentId);

        List<Posting> postings = postingList.postings();

        assertEquals(1, postings.size());
        assertEquals(2, postings.getFirst().termFrequency());
        assertEquals(1, postingList.documentFrequency());
    }

    @Test
    @DisplayName("서로 다른 documentId를 추가하면 documentFrequency가 증가한다")
    void increasesDocumentFrequencyWhenDifferentDocumentIdsAreAdded() {
        PostingList postingList = new PostingList();
        postingList.add(1L);
        postingList.add(2L);

        List<Posting> postings = postingList.postings();
        assertEquals(2, postingList.documentFrequency());
        assertEquals(
                List.of(
                        new Posting(1L, 1),
                        new Posting(2L, 1)
                ),
                postings
        );
    }

    @Test
    @DisplayName("posting 목록은 documentId 오름차순으로 반환된다")
    void returnsPostingsSortedByDocumentId() {
        PostingList postingList = new PostingList();
        postingList.add(3L);
        postingList.add(1L);
        postingList.add(2L);

        List<Posting> postings = postingList.postings();

        assertEquals(
                List.of(new Posting(1L, 1), new Posting(2L, 1), new Posting(3L, 1)),
                postings
        );
    }

    @Test
    @DisplayName("반환된 posting 목록은 외부에서 수정할 수 없다")
    void returnsImmutablePostings() {
        PostingList postingList = new PostingList();
        postingList.add(1L);
        postingList.add(2L);

        List<Posting> postings = postingList.postings();
        assertThrows(UnsupportedOperationException.class, () -> postings.add(new Posting(3, 1)));
    }

    @Test
    @DisplayName("documentId가 양수가 아니면 posting에 추가할 수 없다")
    void rejectsNonPositiveDocumentId() {
        PostingList postingList = new PostingList();

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> postingList.add(0L)),
                () -> assertThrows(IllegalArgumentException.class, () -> postingList.add(-1L))
        );
    }
}
