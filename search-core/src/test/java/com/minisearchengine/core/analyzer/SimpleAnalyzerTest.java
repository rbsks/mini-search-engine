package com.minisearchengine.core.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SimpleAnalyzerTest {

    private final SimpleAnalyzer analyzer = new SimpleAnalyzer();

    @Test
    @DisplayName("대소문자를 정규화하고 문자와 숫자만 토큰으로 분리한다")
    void analyzesTextWithLowercaseAndLetterNumberTokens() {
        List<String> tokens = analyzer.analyze("Mini Search AI, Doc-123 검색 ENGINE!");

        assertEquals(List.of("mini", "search", "ai", "doc", "123", "검색", "engine"), tokens);
    }

    @Test
    @DisplayName("공백과 구두점만 있는 텍스트는 빈 토큰 목록을 반환한다")
    void ignoresBlankAndPunctuationOnlyText() {
        assertEquals(List.of(), analyzer.analyze("  , / --- \n\t"));
    }

    @Test
    @DisplayName("토큰화 이후 등록된 토큰 필터를 순서대로 적용한다")
    void appliesTokenFiltersAfterTokenization() {
        SimpleAnalyzer filteredAnalyzer = new SimpleAnalyzer(List.of(tokens -> tokens.stream()
                .filter(token -> token.length() > 2)
                .toList()));

        List<String> tokens = filteredAnalyzer.analyze("AI search core v1");

        assertEquals(List.of("search", "core"), tokens);
    }

    @Test
    @DisplayName("분석 결과 토큰 목록은 외부에서 변경할 수 없다")
    void returnsImmutableTokens() {
        List<String> tokens = analyzer.analyze("search core");

        assertThrows(UnsupportedOperationException.class, () -> tokens.add("mutate"));
    }

    @Test
    @DisplayName("분석 대상 텍스트가 null이면 예외를 던진다")
    void rejectsNullText() {
        assertThrows(NullPointerException.class, () -> analyzer.analyze(null));
    }
}
