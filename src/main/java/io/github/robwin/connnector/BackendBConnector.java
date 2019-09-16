package io.github.robwin.connnector;


import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.robwin.exception.BusinessException;
import io.vavr.control.Try;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static io.github.resilience4j.bulkhead.annotation.Bulkhead.*;

@RateLimiter(name = "backendB")
@Retry(name = "backendB")
@Component(value = "backendBConnector")
public class BackendBConnector implements Connector {

    @Override
    @Bulkhead(name = "backendB")
    public String failure() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "This is a remote exception");
    }

    @Override
    @Bulkhead(name = "backendB")
    public String success() {
        return "Hello World from backend B";
    }

    @Override
    public String successException() {
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "This is a remote client exception");
    }

    @Override
    public String ignoreException() {
        throw new BusinessException("This exception is ignored by the CircuitBreaker of backend B");
    }

    @Override
    @Bulkhead(name = "backendB")
    public Flux<String> fluxFailure() {
        return Flux.error(new IOException("BAM!"));
    }

    @Override
    @Bulkhead(name = "backendB")
    public Mono<String> monoSuccess() {
        return Mono.just("Hello World from backend B");
    }

    @Override
    @Bulkhead(name = "backendB")
    public Mono<String> monoFailure() {
        return Mono.error(new IOException("BAM!"));
    }

    @Override
    @Bulkhead(name = "backendB")
    public Flux<String> fluxSuccess() {
        return Flux.just("Hello", "World");
    }

    @Override
    @Bulkhead(name = "backendB", type = Type.THREADPOOL)
    public CompletableFuture<String> futureSuccess() {
        return CompletableFuture.completedFuture("Hello World from backend B");
    }

    @Override
    @Bulkhead(name = "backendB", type = Type.THREADPOOL)
    public CompletableFuture<String> futureFailure() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new IOException("BAM!"));
        return future;
    }

    @Override
    public String failureWithFallback() {
        return Try.ofSupplier(this::failure).recover(ex -> "Recovered").get();
    }
}
