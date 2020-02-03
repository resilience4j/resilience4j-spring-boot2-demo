package io.github.robwin.service;


import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.github.robwin.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static io.github.resilience4j.bulkhead.annotation.Bulkhead.Type;

@CircuitBreaker(name = "backendC")
@Retry(name = "backendC")
@Component(value = "backendCService")
public class BackendCService implements Service {

    @Override
    @Bulkhead(name = "backendC")
    public String failure() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "This is a remote exception");
    }

    @Override
    @Bulkhead(name = "backendC")
    public String success() {
        return "Hello World from backend C";
    }

    @Override
    public String successException() {
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "This is a remote client exception");
    }

    @Override
    public String ignoreException() {
        throw new BusinessException("This exception is ignored by the CircuitBreaker of backend C");
    }

    @Override
    @Bulkhead(name = "backendC")
    public Flux<String> fluxFailure() {
        return Flux.error(new IOException("BAM!"));
    }

    @Override
    @TimeLimiter(name = "backendC", fallbackMethod = "fluxFallback")
    public Flux<String> fluxTimeout() {
        return Flux.
                just("Hello World from backend A")
                .delayElements(Duration.ofSeconds(10));
    }

    @Override
    @Bulkhead(name = "backendC")
    public Mono<String> monoSuccess() {
        return Mono.just("Hello World from backend C");
    }

    @Override
    @Bulkhead(name = "backendC")
    public Mono<String> monoFailure() {
        return Mono.error(new IOException("BAM!"));
    }

    @Override
    @TimeLimiter(name = "backendC", fallbackMethod = "monoFallback")
    public Mono<String> monoTimeout() {
        return Mono.
                just("Hello World from backend A")
                .delayElement(Duration.ofSeconds(10));
    }

    @Override
    @Bulkhead(name = "backendC")
    public Flux<String> fluxSuccess() {
        return Flux.just("Hello", "World");
    }

    @Override
    @Bulkhead(name = "backendC", type = Type.THREADPOOL)
    public CompletableFuture<String> futureSuccess() {
        return CompletableFuture.completedFuture("Hello World from backend C");
    }

    @Override
    @Bulkhead(name = "backendC", type = Type.THREADPOOL)
    public CompletableFuture<String> futureFailure() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new IOException("BAM!"));
        return future;
    }

    @Override
    public CompletableFuture<String> futureWithFallback() {
        return null;
    }

    @Override
    @Bulkhead(name = "backendA", type = Type.THREADPOOL)
    @TimeLimiter(name = "backendA", fallbackMethod = "fallback")
    public CompletableFuture<String> futureTimeout() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @CircuitBreaker(name = "backendC", fallbackMethod = "futureFallback")
    public String failureWithFallback() {
        return failure();
    }

    private String fallback(Throwable ex) {
        return "Recovered " + ex.toString();
    }

    private CompletableFuture<String> futureFallback(Throwable ex) {
        return CompletableFuture.completedFuture("Recovered: " + ex.toString());
    }

    private Mono<String> monoFallback(Throwable ex) {
        return Mono.just("Recovered: " + ex.toString());
    }

    private Flux<String> fluxFallback(Throwable ex) {
        return Flux.just("Recovered: " + ex.toString());
    }
}
