package com.github.tachesimazzoca.akka.examples.spring;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import org.springframework.context.ApplicationContext;

public class InjectedActorProducer implements IndirectActorProducer {
    private final ApplicationContext applicationContext;
    private final String actorName;

    public InjectedActorProducer(ApplicationContext applicationContext, String actorName) {
        this.applicationContext = applicationContext;
        this.actorName = actorName;
    }

    @Override
    public Actor produce() {
        return applicationContext.getBean(actorName, Actor.class);
    }

    @Override
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext.getType(actorName);
    }
}
