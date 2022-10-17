package io.github.robwin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@AutoConfigureMetrics
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class)
public abstract class AbstractIntegrationTest {

    protected static final String BACKEND_A = "backendA";
    protected static final String BACKEND_B = "backendB";

    @Autowired
    protected CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected WebTestClient webClient;

    @BeforeEach
    public void setup() {
        transitionToClosedState(BACKEND_A);
        transitionToClosedState(BACKEND_B);
    }

    protected void transitionToOpenState(String circuitBreakerName) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
        circuitBreaker.transitionToOpenState();
    }

    protected void transitionToClosedState(String circuitBreakerName) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
        circuitBreaker.transitionToClosedState();
    }

}
