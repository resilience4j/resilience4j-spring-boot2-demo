package io.github.robwin.controller;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
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
        return Decorators.ofSupplier(businessBService::failure)
            .withCircuitBreaker(circuitBreaker)
            .withBulkhead(bulkhead)
            .withRetry(retry)
            .get();
    }

    @GetMapping("success")
    public String success(){
        return Decorators.ofSupplier(businessBService::success)
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .withRetry(retry)
                .get();
    }

    @GetMapping("successException")
    public String successException(){
        return Decorators.ofSupplier(businessBService::successException)
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .get();
    }

    @GetMapping("ignore")
    public String ignore(){
        return Decorators.ofSupplier(businessBService::ignoreException)
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead).get();
    }

    @GetMapping("monoSuccess")
    public Mono<String> monoSuccess(){
        return businessBService.monoSuccess()
            .transform(TimeLimiterOperator.of(timeLimiter))
            .transform(CircuitBreakerOperator.of(circuitBreaker))
            .transform(BulkheadOperator.of(bulkhead))
            .transform(RetryOperator.of(retry));
    }

    @GetMapping("monoFailure")
    public Mono<String> monoFailure() {
        return businessBService.monoFailure()
                .transform(TimeLimiterOperator.of(timeLimiter))
                .transform(CircuitBreakerOperator.of(circuitBreaker))
                .transform(BulkheadOperator.of(bulkhead))
                .transform(RetryOperator.of(retry));
    }

    @GetMapping("fluxSuccess")
    public Flux<String> fluxSuccess(){
        return businessBService.fluxSuccess()
                .transform(TimeLimiterOperator.of(timeLimiter))
                .transform(CircuitBreakerOperator.of(circuitBreaker))
                .transform(BulkheadOperator.of(bulkhead))
                .transform(RetryOperator.of(retry));
    }

    @GetMapping("fluxFailure")
    public Flux<String> fluxFailure(){
        return businessBService.fluxFailure()
                .transform(TimeLimiterOperator.of(timeLimiter))
                .transform(CircuitBreakerOperator.of(circuitBreaker))
                .transform(BulkheadOperator.of(bulkhead))
                .transform(RetryOperator.of(retry));
    }

    @GetMapping("monoTimeout")
    public Mono<String> monoTimeout(){
        return businessBService.monoTimeout()
                .transform(TimeLimiterOperator.of(timeLimiter))
                .onErrorResume(TimeoutException.class, this::monoFallback)
                .transform(CircuitBreakerOperator.of(circuitBreaker))
                .transform(BulkheadOperator.of(bulkhead));
    }

    @GetMapping("fluxTimeout")
    public Flux<String> fluxTimeout(){
        return businessBService.fluxTimeout()
                .transform(TimeLimiterOperator.of(timeLimiter))
                .onErrorResume(TimeoutException.class, this::fluxFallback)
                .transform(CircuitBreakerOperator.of(circuitBreaker))
                .transform(BulkheadOperator.of(bulkhead));
    }

    @GetMapping("futureFailure")
    public CompletableFuture<String> futureFailure(){
        return Decorators.ofSupplier(businessBService::failure)
                .withThreadPoolBulkhead(threadPoolBulkhead)
                .withTimeLimiter(timeLimiter, scheduledExecutorService)
                .withCircuitBreaker(circuitBreaker)
                .withRetry(retry, scheduledExecutorService)
                .get().toCompletableFuture();
    }

    @GetMapping("futureSuccess")
    public CompletableFuture<String> futureSuccess(){
        return Decorators.ofSupplier(businessBService::success)
                .withThreadPoolBulkhead(threadPoolBulkhead)
                .withTimeLimiter(timeLimiter, scheduledExecutorService)
                .withCircuitBreaker(circuitBreaker)
                .withRetry(retry, scheduledExecutorService)
                .get().toCompletableFuture();
    }

    @GetMapping("futureTimeout")
    public CompletableFuture<String> futureTimeout(){
        return Decorators.ofSupplier(this::timeout)
                .withThreadPoolBulkhead(threadPoolBulkhead)
                .withTimeLimiter(timeLimiter, scheduledExecutorService)
                .withFallback(TimeoutException.class, this::fallback)
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .get().toCompletableFuture();
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
