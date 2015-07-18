package com.github.tachesimazzoca.akka.examples.hello;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Greeter extends UntypedActor {
    public static final Request request = new Request();

    private static class Request {
    }

    public static class Response {
        public final String body;

        public Response(String body) {
            this.body = body;
        }
    }

    @Override
    public void preStart() {
        System.out.println(getClass().getName() + "#preStart()");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Request) {
            ActorRef sender = getSender();
            sender.tell(new Response("Hello " + sender), getSelf());
        } else {
            unhandled(message);
        }
    }

    @Override
    public void postStop() {
        System.out.println(getClass().getName() + "#postStop");
    }
}
