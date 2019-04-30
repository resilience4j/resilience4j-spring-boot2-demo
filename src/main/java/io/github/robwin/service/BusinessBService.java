package io.github.robwin.service;


import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.robwin.connnector.Connector;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@Service(value = "businessBService")
public class BusinessBService implements BusinessService  {

    private final Connector backendBConnector;
    private final CircuitBreaker circuitBreaker;

    public BusinessBService(@Qualifier("backendBConnector") Connector backendBConnector,
                            CircuitBreakerRegistry circuitBreakerRegistry){
        this.backendBConnector = backendBConnector;
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("backendB");
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
                .recover(this::recovery);
    }

    @Override
    public Flux<String> fluxFailure() {
        return backendBConnector.fluxFailure()
                .transform(CircuitBreakerOperator.of(circuitBreaker));
    }

    private String recovery(Throwable throwable) {
        // Handle exception and invoke fallback
        return "Hello world from recovery";
    }
}
