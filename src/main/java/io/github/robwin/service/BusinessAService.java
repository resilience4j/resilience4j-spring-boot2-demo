package io.github.robwin.service;

import io.github.robwin.connnector.Connector;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

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
    public String successException() {
        return backendAConnector.successException();
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
    public CompletableFuture<String> futureSuccess() {
        return backendAConnector.futureSuccess();
    }

    @Override
    public CompletableFuture<String> futureFailure() {
        return backendAConnector.futureFailure();
    }

    @Override
    public String failureWithFallback() {
        return backendAConnector.failureWithFallback();
    }
}
