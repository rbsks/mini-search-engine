package com.minisearchengine.core.analyzer.port;

import java.util.List;

/**
 * 문서 본문이나 검색어 같은 원문 문자열을 검색 엔진이 사용할 수 있는 토큰 목록으로 변환하는 계약이다.
 *
 * <p>Analyzer는 색인 저장 구조나 랭킹 공식을 알지 않는다. 이 계층의 책임은 오직 텍스트를 정규화하고
 * 검색 가능한 단위로 나누는 것이다. 실제 색인 단계에서는 {@code SearchEngine.index(...)}가 이 인터페이스를
 * 호출해 문서 본문을 토큰으로 만들고, 검색 단계에서는 {@code SearchEngine.search(...)}가 검색어를 토큰으로 만든다.
 */
public interface Analyzer {

    /**
     * 입력 텍스트를 정규화된 토큰 목록으로 분석한다.
     *
     * <p>구현체는 대소문자 정규화, 토큰 분리, 필터 적용 같은 세부 정책을 결정한다. 반환된 토큰은
     * 이후 Inverted Index에 저장되거나 query term으로 사용될 수 있어야 한다.
     */
    List<String> analyze(String text);
}
