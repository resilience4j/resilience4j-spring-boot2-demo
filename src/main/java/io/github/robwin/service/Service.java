package io.github.robwin.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

public interface Service {
    String failure();

    String failureWithFallback();

    String success();

    String successException();

    String ignoreException();

    Flux<String> fluxSuccess();

    Flux<String> fluxFailure();

    Flux<String> fluxTimeout();

    Mono<String> monoSuccess();

    Mono<String> monoFailure();

    Mono<String> monoTimeout();

    CompletableFuture<String> futureSuccess();

    CompletableFuture<String> futureFailure();

    CompletableFuture<String> futureTimeout();

}
