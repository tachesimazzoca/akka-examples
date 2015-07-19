package com.github.tachesimazzoca.akka.examples.timeout;

import akka.actor.ActorRef;
import akka.actor.Status;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import scala.concurrent.Future;

import java.util.ArrayList;
import java.util.List;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

public class Application extends UntypedActor {
    public static class Result {
        public final List<String> rows;

        public Result(List<String> rows) {
            this.rows = rows;
        }
    }

    @Override
    public void preStart() {
        pipe(safeOperation(), getContext().dispatcher()).to(getSelf());
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Result) {
            // receive the result of safeOperation
            Result result = (Result) message;
            System.out.println(result.rows);
            pipe(timeoutOperation(), getContext().dispatcher()).to(getSelf());
        } else if (message instanceof Status.Failure) {
            // receive the exception of timeoutOperation
            Status.Failure failure = (Status.Failure) message;
            System.out.println(failure.cause().getMessage());
            getContext().stop(getSelf());
        } else {
            unhandled(message);
        }
    }

    private Future<Result> aggregateJobs(List<Future<Object>> jobs) {
        Future<Iterable<Object>> aggregate = Futures.sequence(jobs, getContext().dispatcher());
        Future<Result> future = aggregate.map(
                new Mapper<Iterable<Object>, Result>() {
                    @Override
                    public Result apply(Iterable<Object> args) {
                        List<String> rows = new ArrayList<String>();
                        for (Object arg : args) {
                            rows.add(arg.toString());
                        }
                        return new Result(rows);
                    }
                },
                getContext().dispatcher());
        return future;
    }

    private Future<Result> safeOperation() {
        List<Future<Object>> jobs = new ArrayList<Future<Object>>();

        ActorRef worker1 = getContext().actorOf(Worker.props(500L));
        Future<Object> future1 = ask(worker1, "Job1", 1000L);
        jobs.add(future1);

        ActorRef worker2 = getContext().actorOf(Worker.props(200L));
        Future<Object> future2 = ask(worker2, "Job2", 1000L);
        jobs.add(future2);

        return aggregateJobs(jobs);
    }

    private Future<Result> timeoutOperation() {
        List<Future<Object>> jobs = new ArrayList<Future<Object>>();

        ActorRef worker1 = getContext().actorOf(Worker.props(500L));
        Future<Object> future1 = ask(worker1, "Job1", 1000L);
        jobs.add(future1);

        ActorRef worker2 = getContext().actorOf(Worker.props(1500L));
        Future<Object> future2 = ask(worker2, "Job2", 1000L);
        jobs.add(future2);

        return aggregateJobs(jobs);
    }
}
