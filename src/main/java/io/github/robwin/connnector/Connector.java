package io.github.robwin.connnector;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

public interface Connector {
    String failure();

    String success();

    String successException();

    String ignoreException();

    Flux<String> fluxFailure();

    Mono<String> monoSuccess();

    Mono<String> monoFailure();

    Flux<String> fluxSuccess();

    String failureWithFallback();

    CompletableFuture<String> futureSuccess();

    CompletableFuture<String> futureFailure();

}
