package com.github.tachesimazzoca.akka.examples.counter;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import java.util.concurrent.atomic.AtomicInteger;

public class Application extends UntypedActor {
    private ActorRef counter;

    private int nonAtomicCount;
    volatile private int volatileCount;
    private AtomicInteger atomicCount;

    @Override
    public void preStart() throws InterruptedException {
        counter = getContext().actorOf(Props.create(Counter.class), "counter");

        nonAtomicCount = 0;
        volatileCount = 0;
        atomicCount = new AtomicInteger(0);

        Thread th1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    counter.tell(Counter.Message.Increment, getSelf());
                    nonAtomicCount++;
                    volatileCount++;
                    atomicCount.incrementAndGet();
                }
            }
        });

        Thread th2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    counter.tell(Counter.Message.Increment, getSelf());
                    nonAtomicCount++;
                    volatileCount++;
                    atomicCount.incrementAndGet();
                }
            }
        });

        th1.start();
        th2.start();

        th1.join();
        th2.join();

        System.out.println("nonAtomicCount: " + nonAtomicCount);
        System.out.println("volatileCount: " + volatileCount);
        System.out.println("atomicCount: " + atomicCount.get());
        counter.tell(Counter.Message.Get, getSelf());
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Integer) {
            System.out.println("count: " + message);
            getContext().stop(getSelf());
        } else {
            unhandled(message);
        }
    }
}
