package com.minisearchengine.core.analyzer;

import java.util.List;

/**
 * 토큰화가 끝난 뒤 토큰 목록을 한 번 더 변환하기 위한 확장 지점이다.
 *
 * <p>예를 들어 불용어 제거, stemming, 너무 짧은 토큰 제거 같은 정책을 Analyzer 본체에 직접 넣지 않고
 * 작은 필터로 분리할 수 있다. 필터는 순서대로 적용되므로, 여러 정책을 조합할 때는 등록 순서가 결과에 영향을 준다.
 */
@FunctionalInterface
public interface TokenFilter {

    /**
     * 이미 토큰화된 목록에 하나의 변환 규칙을 적용한다.
     *
     * <p>구현체는 입력 목록을 직접 수정하기보다 새 목록을 반환하는 방식이 안전하다. {@link SimpleAnalyzer}는
     * 각 필터 결과를 불변 목록으로 복사해 이후 단계에서 토큰 목록이 의도치 않게 바뀌지 않도록 방어한다.
     */
    List<String> apply(List<String> tokens);
}
