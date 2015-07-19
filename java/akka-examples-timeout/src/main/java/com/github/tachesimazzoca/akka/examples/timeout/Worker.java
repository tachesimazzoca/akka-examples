package com.github.tachesimazzoca.akka.examples.timeout;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

public class Worker extends UntypedActor {
    private final long time;

    public Worker() {
        time = 0L;
    }

    public Worker(long time) {
        this.time = time;
    }

    @Override
    public void onReceive(Object message) throws InterruptedException {
        String name = getSelf().path().name();
        System.out.println(String.format("%s is working.", name));
        Thread.sleep(time);
        System.out.println(String.format(
                "%s finished after %d msec.", name, time));
        getSender().tell(message, getSelf());
    }

    public static Props props(final long sleep) {
        return Props.create(new Creator<Worker>() {
            @Override
            public Worker create() throws Exception {
                return new Worker(sleep);
            }
        });
    }
}
