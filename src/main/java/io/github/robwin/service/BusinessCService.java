package io.github.robwin.service;


import io.github.robwin.connnector.Connector;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service(value = "businessCService")
public class BusinessCService implements BusinessService  {

    private final Connector backendCConnector;

    public BusinessCService(@Qualifier("backendCConnector") Connector backendCConnector){
        this.backendCConnector = backendCConnector;
    }

    @Override
    public String failure() {
        return backendCConnector.failure();
    }

    @Override
    public String success() {
        return backendCConnector.success();
    }

    @Override
    public String ignore() {
        return backendCConnector.ignoreException();
    }


    @Override
    public Flux<String> fluxFailure() {
        return backendCConnector.fluxFailure();
    }

    @Override
    public Mono<String> monoSuccess() {
        return backendCConnector.monoSuccess();
    }

    @Override
    public Mono<String> monoFailure() {
        return backendCConnector.monoFailure();
    }

    @Override
    public Flux<String> fluxSuccess() {
        return backendCConnector.fluxSuccess();
    }

    @Override
    public String failureWithFallback() {
        return backendCConnector.failureWithFallback();
    }
}
