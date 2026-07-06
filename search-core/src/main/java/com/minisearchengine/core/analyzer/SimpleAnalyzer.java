package com.minisearchengine.core.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Phase 01에서 사용하는 기본 Analyzer 구현체다.
 *
 * <p>현재 정책은 단순하다. 입력을 {@link Locale#ROOT} 기준으로 소문자화한 뒤, 문자와 숫자가 이어진 구간만
 * 하나의 토큰으로 추출한다. 공백, 구두점, 기호는 토큰 경계로 취급한다. 이 구현은 색인 구조를 전혀 알지 않고,
 * 나중에 stop-word 제거, stemming 같은 정책이 필요하면 {@link TokenFilter}를 추가해서 확장한다.
 */
public final class SimpleAnalyzer implements Analyzer {

    private final List<TokenFilter> tokenFilters;

    public SimpleAnalyzer() {
        this(List.of());
    }

    public SimpleAnalyzer(List<TokenFilter> tokenFilters) {
        this.tokenFilters = List.copyOf(Objects.requireNonNull(tokenFilters, "tokenFilters must not be null"));
    }

    @Override
    public List<String> analyze(String text) {
        Objects.requireNonNull(text, "text must not be null");

        List<String> tokens = tokenize(text.toLowerCase(Locale.ROOT));
        for (TokenFilter tokenFilter : tokenFilters) {
            // 사용자 정의 필터가 변경 가능한 List를 반환하더라도 다음 단계로 mutable 상태가 새어 나가지 않게 복사한다.
            tokens = List.copyOf(Objects.requireNonNull(tokenFilter.apply(tokens), "filtered tokens must not be null"));
        }
        return List.copyOf(tokens);
    }

    private static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        // char 단위가 아니라 code point 단위로 순회해 한글 같은 비 ASCII 문자도 문자 토큰으로 안전하게 처리한다.
        text.codePoints().forEach(codePoint -> {
            if (Character.isLetterOrDigit(codePoint)) {
                currentToken.appendCodePoint(codePoint);
                return;
            }
            if (!currentToken.isEmpty()) {
                tokens.add(currentToken.toString());
                currentToken.setLength(0);
            }
        });

        if (!currentToken.isEmpty()) {
            tokens.add(currentToken.toString());
        }
        return tokens;
    }
}
