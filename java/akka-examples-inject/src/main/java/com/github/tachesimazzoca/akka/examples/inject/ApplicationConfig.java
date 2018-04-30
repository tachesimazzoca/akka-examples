package com.github.tachesimazzoca.akka.examples.inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan
public class ApplicationConfig {
    @Bean
    public TranslationService translationService() {
        Map<String, String> translationMap = new HashMap<String, String>();
        translationMap.put("hello", "こんにちは");
        return new TranslationServiceImpl(translationMap);
    }
}
