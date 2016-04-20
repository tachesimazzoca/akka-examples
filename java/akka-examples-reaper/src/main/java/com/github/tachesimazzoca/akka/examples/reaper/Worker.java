package com.github.tachesimazzoca.akka.examples.reaper;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Worker extends UntypedActor {
    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Integer) {
            Integer n = (Integer) message;
            if (n > 0) {
                long sleepTime = 1000L * (new Random().nextInt(5) + 1);
                log.info(String.format("n: %d, sleepTime: %d", n, sleepTime));
                getContext().system().scheduler().scheduleOnce(
                        Duration.create(sleepTime, TimeUnit.MILLISECONDS),
                        getSelf(), n - 1, getContext().dispatcher(),
                        getSelf());
            } else {
                getContext().stop(getSelf());
            }

        } else {
            unhandled(message);
        }
    }
}
