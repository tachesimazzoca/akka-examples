package com.github.tachesimazzoca.akka.examples.reaper;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Application extends UntypedActor {
    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void preStart() {
        ActorRef reaper = getContext().actorOf(Props.create(Reaper.class));

        for (int n = 1; n <= 5; n++) {
            ActorRef worker = getContext().actorOf(Props.create(Worker.class), "worker" + n);
            reaper.tell(new Reaper.WatchMe(worker), getSelf());
            worker.tell(5, getSelf());
        }
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (Reaper.Message.REAPED == message) {
            log.info("REAPED from " + getSender());
            getContext().system().shutdown();
        }
    }
}
