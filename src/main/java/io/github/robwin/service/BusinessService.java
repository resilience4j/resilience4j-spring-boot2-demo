package io.github.robwin.service;


import io.vavr.control.Try;
import reactor.core.publisher.Flux;

public interface BusinessService {
    String failure();

    String success();

    String ignore();

    Try<String> methodWithRecovery();

    Flux<String> fluxFailure();
}
