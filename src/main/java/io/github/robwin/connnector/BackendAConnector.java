package io.github.robwin.connnector;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.robwin.exception.BusinessException;
import reactor.core.publisher.Flux;

/**
 * This Connector shows how to use the CircuitBreaker annotation.
 */
@CircuitBreaker(backend = "backendA")
@Component(value = "backendAConnector")
public class BackendAConnector implements Connector {

    @Override
    public String failure() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "This is a remote exception");
    }

    @Override
    public String ignoreException() {
        throw new BusinessException("This exception is ignored by the CircuitBreaker of backend A");
    }

    @Override
    public String success() {
        return "Hello World from backend A";
    }

    @Override
    public Flux<String> methodWhichReturnsAStream() {
        return Flux.never();
    }
}
