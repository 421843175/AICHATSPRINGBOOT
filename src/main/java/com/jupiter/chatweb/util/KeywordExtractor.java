package com.jupiter.chatweb.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// KeywordExtractor.java
public class KeywordExtractor {
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "什么", "怎么", "如何", "请问", "呢", "吗", "的", "了", "有"
    ));

    public static List<String> extractKeywords(String question) {
        List<Term> termList = HanLP.segment(question);
        return termList.stream()
                .map(term -> term.word)
                .filter(word -> word.length() > 1 && !STOP_WORDS.contains(word))
                .collect(Collectors.toList());
    }
}