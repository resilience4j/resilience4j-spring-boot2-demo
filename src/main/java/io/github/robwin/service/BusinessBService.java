package io.github.robwin.service;


import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.autoconfigure.CircuitBreakerProperties;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.robwin.connnector.Connector;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;

@Service(value = "businessBService")
public class BusinessBService implements BusinessService  {

    private final Connector backendBConnector;
    private final CircuitBreaker circuitBreaker;

    public BusinessBService(@Qualifier("backendBConnector") Connector backendBConnector,
                            CircuitBreakerRegistry circuitBreakerRegistry, CircuitBreakerProperties circuitBreakerProperties){
        this.backendBConnector = backendBConnector;
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("backendB", () -> circuitBreakerProperties.createCircuitBreakerConfig("backendB"));
    }

    public String failure() {
        return CircuitBreaker.decorateSupplier(circuitBreaker, backendBConnector::failure).get();
    }

    public String success() {
        return CircuitBreaker.decorateSupplier(circuitBreaker, backendBConnector::success).get();
    }

    @Override
    public String ignore() {
        return CircuitBreaker.decorateSupplier(circuitBreaker, backendBConnector::ignoreException).get();
    }

    @Override
    public Try<String> methodWithRecovery() {
        Supplier<String> backendFunction = CircuitBreaker.decorateSupplier(circuitBreaker, () -> backendBConnector.failure());
        return Try.ofSupplier(backendFunction)
                .recover((throwable) -> recovery(throwable));
    }

    public Flux<String> methodWhichReturnsAStream() {
        return backendBConnector.methodWhichReturnsAStream()
                .transform(CircuitBreakerOperator.of(circuitBreaker));
    }

    private String recovery(Throwable throwable) {
        // Handle exception and invoke fallback
        return "Hello world from recovery";
    }
}
