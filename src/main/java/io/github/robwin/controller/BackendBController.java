package io.github.robwin.controller;

import io.github.resilience4j.bulkhead.*;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.github.robwin.service.Service;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

@RestController
@RequestMapping(value = "/backendB")
public class BackendBController {

    private static final String BACKEND_B = "backendB";
    private final Service businessBService;
    private final CircuitBreaker circuitBreaker;
    private final Bulkhead bulkhead;
    private final ThreadPoolBulkhead threadPoolBulkhead;
    private final Retry retry;
    private final RateLimiter rateLimiter;
    private final TimeLimiter timeLimiter;
    private final ScheduledExecutorService scheduledExecutorService;

    public BackendBController(
            @Qualifier("backendBService")Service businessBService,
            CircuitBreakerRegistry circuitBreakerRegistry,
            ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry,
            BulkheadRegistry bulkheadRegistry,
            RetryRegistry retryRegistry,
            RateLimiterRegistry rateLimiterRegistry,
            TimeLimiterRegistry timeLimiterRegistry){
        this.businessBService = businessBService;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(BACKEND_B);
        this.bulkhead = bulkheadRegistry.bulkhead(BACKEND_B);
        this.threadPoolBulkhead = threadPoolBulkheadRegistry.bulkhead(BACKEND_B);
        this.retry = retryRegistry.retry(BACKEND_B);
        this.rateLimiter = rateLimiterRegistry.rateLimiter(BACKEND_B);
        this.timeLimiter = timeLimiterRegistry.timeLimiter(BACKEND_B);
        this.scheduledExecutorService = Executors.newScheduledThreadPool(3);
    }

    @GetMapping("failure")
    public String failure(){
        return execute(businessBService::failure);
    }

    @GetMapping("success")
    public String success(){
        return execute(businessBService::success);
    }

    @GetMapping("successException")
    public String successException(){
        return execute(businessBService::successException);
    }

    @GetMapping("ignore")
    public String ignore(){
        return Decorators.ofSupplier(businessBService::ignoreException)
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead).get();
    }

    @GetMapping("monoSuccess")
    public Mono<String> monoSuccess(){
        return execute(businessBService.monoSuccess());
    }

    @GetMapping("monoFailure")
    public Mono<String> monoFailure() {
        return execute(businessBService.monoFailure());
    }

    @GetMapping("fluxSuccess")
    public Flux<String> fluxSuccess(){
        return execute(businessBService.fluxFailure());
    }

    @GetMapping("fluxFailure")
    public Flux<String> fluxFailure(){
        return execute(businessBService.fluxFailure());
    }

    @GetMapping("monoTimeout")
    public Mono<String> monoTimeout(){
        return executeWithFallback(businessBService.monoTimeout(), this::monoFallback);
    }

    @GetMapping("fluxTimeout")
    public Flux<String> fluxTimeout(){
        return executeWithFallback(businessBService.fluxTimeout(), this::fluxFallback);
    }

    @GetMapping("futureFailure")
    public CompletableFuture<String> futureFailure(){
        return executeAsync(businessBService::failure);
    }

    @GetMapping("futureSuccess")
    public CompletableFuture<String> futureSuccess(){
        return executeAsync(businessBService::success);
    }

    @GetMapping("futureTimeout")
    public CompletableFuture<String> futureTimeout(){
        return executeAsyncWithFallback(this::timeout, this::fallback);
    }

    @GetMapping("fallback")
    public String failureWithFallback(){
        return businessBService.failureWithFallback();
    }

    private String timeout(){
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    private <T> Mono<T> execute(Mono<T> publisher){
        return publisher
                .transform(BulkheadOperator.of(bulkhead))
                .transform(CircuitBreakerOperator.of(circuitBreaker))
                .transform(RetryOperator.of(retry));
    }

    private <T> Flux<T> execute(Flux<T> publisher){
        return publisher
                .transform(BulkheadOperator.of(bulkhead))
                .transform(CircuitBreakerOperator.of(circuitBreaker))
                .transform(RetryOperator.of(retry));
    }


    private <T> Mono<T> executeWithFallback(Mono<T> publisher, Function<Throwable, Mono<T>> fallback){
        return publisher
                .transform(TimeLimiterOperator.of(timeLimiter))
                .transform(BulkheadOperator.of(bulkhead))
                .transform(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(TimeoutException.class, fallback)
                .onErrorResume(CallNotPermittedException.class, fallback)
                .onErrorResume(BulkheadFullException.class, fallback);
    }

    private <T> Flux<T> executeWithFallback(Flux<T> publisher, Function<Throwable, Flux<T>> fallback){
        return publisher
                .transform(TimeLimiterOperator.of(timeLimiter))
                .transform(BulkheadOperator.of(bulkhead))
                .transform(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(TimeoutException.class, fallback)
                .onErrorResume(CallNotPermittedException.class, fallback)
                .onErrorResume(BulkheadFullException.class, fallback);
    }

    private <T> T execute(Supplier<T> supplier){
        return Decorators.ofSupplier(supplier)
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .withRetry(retry)
                .get();
    }

    private <T> CompletableFuture<T> executeAsync(Supplier<T> supplier){
        return Decorators.ofSupplier(supplier)
                .withThreadPoolBulkhead(threadPoolBulkhead)
                .withTimeLimiter(timeLimiter, scheduledExecutorService)
                .withCircuitBreaker(circuitBreaker)
                .withRetry(retry,scheduledExecutorService)
                .get().toCompletableFuture();
    }

    private <T> CompletableFuture<T> executeAsyncWithFallback(Supplier<T> supplier, Function<Throwable, T> fallback){
        return Decorators.ofSupplier(supplier)
                .withThreadPoolBulkhead(threadPoolBulkhead)
                .withTimeLimiter(timeLimiter, scheduledExecutorService)
                .withCircuitBreaker(circuitBreaker)
                .withFallback(asList(TimeoutException.class, CallNotPermittedException.class, BulkheadFullException.class),
                        fallback)
                .get().toCompletableFuture();
    }

    private String fallback(Throwable ex) {
        return "Recovered: " + ex.toString();
    }

    private Mono<String> monoFallback(Throwable ex) {
        return Mono.just("Recovered: " + ex.toString());
    }

    private Flux<String> fluxFallback(Throwable ex) {
        return Flux.just("Recovered: " + ex.toString());
    }
}
