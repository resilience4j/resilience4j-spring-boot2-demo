package io.github.robwin.connnector;

import reactor.core.publisher.Flux;

public interface Connector {
    String failure();

    String success();

    String ignoreException();

    Flux<String> fluxFailure();
}
