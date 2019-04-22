package com.github.tachesimazzoca.akka.examples.http;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.util.concurrent.CompletionStage;

public class HelloServer extends AllDirectives {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("routes");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        HelloServer app = new HelloServer();

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow, ConnectHttp.toHost("localhost", 9000), materializer);

        try {
            System.in.read();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            binding.thenCompose(ServerBinding::unbind)
                    .thenAccept(unbound -> system.terminate());
        }
    }

    private Route createRoute() {
        return concat(
                path("ping", () -> complete("OK")),
                path("time", () -> complete(new java.util.Date().toString())),
                pathEndOrSingleSlash(() -> complete("Hello, this is an example HTTP server with akka-http."))
        );
    }
}
