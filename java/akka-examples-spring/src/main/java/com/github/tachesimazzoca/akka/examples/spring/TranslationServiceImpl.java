package com.github.tachesimazzoca.akka.examples.spring;

import java.util.Map;

public class TranslationServiceImpl implements TranslationService {
    private final Map<String, String> translationMap;

    public TranslationServiceImpl(Map<String, String> translationMap) {
        this.translationMap = translationMap;
    }

    @Override
    public String translate(String source) {
        return translationMap.get(source);
    }
}
