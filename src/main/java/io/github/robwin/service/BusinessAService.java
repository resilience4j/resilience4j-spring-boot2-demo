package io.github.robwin.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.github.robwin.connnector.Connector;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service(value = "businessAService")
public class BusinessAService implements BusinessService {

    private final Connector backendAConnector;

    public BusinessAService(@Qualifier("backendAConnector") Connector backendAConnector){
        this.backendAConnector = backendAConnector;
    }

    @Override
    public String failure() {
        return backendAConnector.failure();
    }

    @Override
    public String success() {
        return backendAConnector.success();
    }

    @Override
    public String ignore() {
        return backendAConnector.ignoreException();
    }

    @Override
    public Flux<String> fluxFailure() {
        return backendAConnector.fluxFailure();
    }

    @Override
    public Mono<String> monoSuccess() {
        return backendAConnector.monoSuccess();
    }

    @Override
    public Mono<String> monoFailure() {
        return backendAConnector.monoFailure();
    }

    @Override
    public Flux<String> fluxSuccess() {
        return backendAConnector.fluxSuccess();
    }

    @Override
    public Try<String> methodWithRecovery() {
        return Try.of(backendAConnector::failure)
                .recover((throwable) -> recovery());
    }

    private String recovery() {
        return "Hello world from recovery";
    }
}
