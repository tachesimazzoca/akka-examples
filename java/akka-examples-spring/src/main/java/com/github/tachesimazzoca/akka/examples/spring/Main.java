package com.github.tachesimazzoca.akka.examples.spring;

import akka.actor.ActorSystem;
import akka.actor.Props;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class Main {
    public static void main(String[] args) {
        AbstractApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(ApplicationConfig.class);
        applicationContext.registerShutdownHook();

        ActorSystem system = ActorSystem.create();
        InjectedActorExtension.Provider.get(system).initialize(applicationContext);

        system.actorOf(Props.create(Application.class));
    }
}
