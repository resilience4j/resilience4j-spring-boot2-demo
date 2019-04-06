package io.github.robwin.connnector;


import java.io.IOException;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import io.github.robwin.exception.BusinessException;
import reactor.core.publisher.Flux;

@Bulkhead(name = "backendB")
@Retry(name = "backendB")
@Component(value = "backendBConnector")
public class BackendBConnector implements Connector {

    @Override
    public String failure() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "This is a remote exception");
    }

    @Override
    public String success() {
        return "Hello World from backend B";
    }

    @Override
    public String ignoreException() {
        throw new BusinessException("This exception is ignored by the CircuitBreaker of backend B");
    }

    @Override
    public Flux<String> methodWhichReturnsAStream() {
        return Flux.error(new IOException("BAM!"));
    }
}
