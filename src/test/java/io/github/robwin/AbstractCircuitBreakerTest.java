package io.github.robwin;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractCircuitBreakerTest {

    protected static final String BACKEND_A = "backendA";
    protected static final String BACKEND_B = "backendB";

    @Autowired
    protected CircuitBreakerRegistry registry;

    @Before
    public void setup(){
        transitionToClosedState(BACKEND_A);
        transitionToClosedState(BACKEND_B);
    }

    protected void checkHealthStatus(String circuitBreakerName, CircuitBreaker.State state) {
        CircuitBreaker circuitBreaker = registry.circuitBreaker(circuitBreakerName);
        assertThat(circuitBreaker.getState()).isEqualTo(state);
    }

    protected void transitionToOpenState(String circuitBreakerName) {
        CircuitBreaker circuitBreaker = registry.circuitBreaker(circuitBreakerName);
        if(!circuitBreaker.getState().equals(CircuitBreaker.State.OPEN)){
            circuitBreaker.transitionToOpenState();
        }
    }

    protected void transitionToClosedState(String circuitBreakerName) {
        CircuitBreaker circuitBreaker = registry.circuitBreaker(circuitBreakerName);
        if(!circuitBreaker.getState().equals(CircuitBreaker.State.CLOSED)){
            circuitBreaker.transitionToClosedState();
        }
    }
}
