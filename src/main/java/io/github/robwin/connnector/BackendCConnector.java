package io.github.robwin.connnector;


import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.robwin.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import reactor.core.publisher.Flux;

import java.io.IOException;

@CircuitBreaker(name = "backendC")
@Bulkhead(name = "backendC")
@Retry(name = "backendC")
@Component(value = "backendCConnector")
public class BackendCConnector implements Connector {

    @Override
    public String failure() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "This is a remote exception");
    }

    @Override
    public String success() {
        return "Hello World from backend C";
    }

    @Override
    public String ignoreException() {
        throw new BusinessException("This exception is ignored by the CircuitBreaker of backend C");
    }

    @Override
    public Flux<String> fluxFailure() {
        return Flux.error(new IOException("BAM!"));
    }
}
