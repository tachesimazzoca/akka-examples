package com.github.tachesimazzoca.akka.examples.spring;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Application extends UntypedActor {

    private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private ActorRef translator;

    @Override
    public void preStart() throws InterruptedException {
        translator = getContext().actorOf(
                InjectedActorExtension.Provider.get(getContext().system()).props("translator"));
        translator.tell("hello", getSelf());
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Translator.Result) {
            logger.info(message.toString());
            getContext().stop(getSelf());
            getContext().system().shutdown();
        } else {
            unhandled(message);
        }
    }
}

