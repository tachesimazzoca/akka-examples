package com.github.tachesimazzoca.akka.examples.sleep;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.HashSet;
import java.util.Set;

public class Application extends UntypedActor {
    private final Set<ActorRef> workers = new HashSet<ActorRef>();

    @Override
    public void preStart() {
        long[] times = {1000L, 0L, 500L};
        for (int i = 0; i < times.length; i++) {
            int n = i + 1;
            ActorRef worker = getContext().actorOf(Sleeper.props(), "worker" + n);
            worker.tell(new Sleeper.Request("Job" + n, times[i]), getSelf());
            workers.add(worker);
        }
    }

    @Override
    public void onReceive(Object message) {
        ActorRef sender = getSender();
        if (workers.contains(sender)) {
            workers.remove(sender);
            if (workers.isEmpty())
                getContext().stop(getSelf());
        } else {
            unhandled(message);
        }
    }
}
