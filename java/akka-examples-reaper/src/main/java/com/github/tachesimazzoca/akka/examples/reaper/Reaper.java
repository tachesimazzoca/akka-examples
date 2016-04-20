package com.github.tachesimazzoca.akka.examples.reaper;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

import java.util.HashSet;
import java.util.Set;

public class Reaper extends UntypedActor {
    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final Set<ActorRef> actorRefSet;

    public static class WatchMe {
        public final ActorRef ref;

        public WatchMe(ActorRef ref) {
            this.ref = ref;
        }
    }

    public enum Message {
        REAPED
    }

    public Reaper() {
        actorRefSet = new HashSet<ActorRef>();
    }

    @Override
    public void preStart() {
        getContext().become(watching);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        unhandled(message);
    }

    private Procedure<Object> watching = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
            if (message instanceof WatchMe) {
                log.info("WatchMe: " + getSender());

                actorRefSet.add(getContext().watch(((WatchMe) message).ref));

            } else if (message instanceof Terminated) {
                log.info("Terminated: " + getSender());

                actorRefSet.remove(getSender());
                log.info(actorRefSet.toString());

                if (actorRefSet.isEmpty()) {
                    log.info("Stopping ...");
                    getContext().parent().tell(Message.REAPED, getSelf());
                    getContext().become(stopping);
                }

            } else {
                unhandled(message);
            }
        }
    };

    private Procedure<Object> stopping = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
            unhandled(message);
        }
    };
}
