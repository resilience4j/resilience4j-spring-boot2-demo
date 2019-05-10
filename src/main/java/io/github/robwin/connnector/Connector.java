package io.github.robwin.connnector;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Connector {
    String failure();

    String success();

    String ignoreException();

    Flux<String> fluxFailure();

    Mono<String> monoSuccess();

    Mono<String> monoFailure();

    Flux<String> fluxSuccess();

    String failureWithFallback();

}
