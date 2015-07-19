package com.github.tachesimazzoca.akka.examples.counter;

import akka.actor.UntypedActor;

public class Counter extends UntypedActor {
    private Integer counter = 0;

    public static enum Message {
        Reset, Increment, Get;
    }

    @Override
    public void onReceive(Object message) {
        if (message == Message.Reset) {
            counter = 0;
        } else if (message == Message.Increment) {
            counter++;
        } else if (message == Message.Get) {
            getSender().tell(counter, getSelf());
        } else {
            unhandled(message);
        }
    }
}
