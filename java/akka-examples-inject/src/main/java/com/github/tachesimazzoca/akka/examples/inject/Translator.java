package com.github.tachesimazzoca.akka.examples.inject;

import akka.actor.UntypedActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class Translator extends UntypedActor {

    private final TranslationService translationService;

    @Autowired
    public Translator(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void onReceive(Object msg) {
        String source = msg.toString();
        String translated = translationService.translate(source);
        getSender().tell(new Result(source, translated), getSelf());
    }

    public static class Result {
        public String source;
        public String translated;

        public Result(String source, String translated) {
            this.source = source;
            this.translated = translated;
        }

        @Override
        public String toString() {
            return source + " > " + translated;
        }
    }
}

