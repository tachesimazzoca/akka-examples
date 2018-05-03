package com.github.tachesimazzoca.akka.examples.spring;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.context.ApplicationContext;

public class InjectedActorExtension extends AbstractExtensionId<InjectedActorExtension.InjectedActorImpl> {

    public static InjectedActorExtension Provider = new InjectedActorExtension();

    @Override
    public InjectedActorImpl createExtension(ExtendedActorSystem system) {
        return new InjectedActorImpl();
    }

    public static class InjectedActorImpl implements Extension {
        private volatile ApplicationContext applicationContext;

        synchronized public void initialize(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        public Props props(String actorName) {
            return Props.create(InjectedActorProducer.class, applicationContext, actorName);
        }
    }
}
