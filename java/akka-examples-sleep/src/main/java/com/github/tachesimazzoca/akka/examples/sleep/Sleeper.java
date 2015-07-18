package com.github.tachesimazzoca.akka.examples.sleep;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

public class Sleeper extends UntypedActor {
    public static class Request {
        public final Object message;
        public final long time;

        public Request(Object message, long time) {
            this.message = message;
            this.time = time;
        }
    }

    @Override
    public void onReceive(Object message) throws InterruptedException {
        if (message instanceof Request) {
            Request req = (Request) message;
            String name = getSelf().path().name();
            System.out.println(String.format("%s is working.", name));
            Thread.sleep(req.time);
            System.out.println(String.format(
                    "%s finished after %d msec.", name, req.time));
            getSender().tell(req.message, getSelf());
        } else {
            unhandled(message);
        }
    }

    public static Props props() {
        return Props.create(new Creator<Sleeper>() {
            @Override
            public Sleeper create() throws Exception {
                return new Sleeper();
            }
        });
    }
}
