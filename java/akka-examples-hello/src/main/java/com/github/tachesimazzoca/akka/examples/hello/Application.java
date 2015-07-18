package com.github.tachesimazzoca.akka.examples.hello;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Application extends UntypedActor {
    private ActorRef greeter;

    @Override
    public void preStart() {
        System.out.println(getClass().getName() + "#preStart()");
        greeter = getContext().actorOf(Props.create(Greeter.class));
        greeter.tell(Greeter.request, getSelf());
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Greeter.Response) {
            Greeter.Response resp = (Greeter.Response) message;
            System.out.println(resp.body);
            getContext().stop(getSelf());
        } else {
            unhandled(message);
        }
    }

    @Override
    public void postStop() {
        System.out.println(getClass().getName() + "#postStop()");
    }
}
