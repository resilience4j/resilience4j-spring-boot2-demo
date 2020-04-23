package io.github.robwin;

import static io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.vavr.collection.Stream;

public class CircuitBreakerTest extends AbstractCircuitBreakerTest {

	@Test
	public void shouldOpenBackendACircuitBreaker() {
		// When
		Stream.rangeClosed(1,2).forEach((count) -> produceFailure(BACKEND_A));

		// Then
		checkHealthStatus(BACKEND_A, State.OPEN);
	}

	@Test
	public void shouldCloseBackendACircuitBreaker() {
		transitionToOpenState(BACKEND_A);
		circuitBreakerRegistry.circuitBreaker(BACKEND_A).transitionToHalfOpenState();

		// When
		Stream.rangeClosed(1,3).forEach((count) -> produceSuccess(BACKEND_A));

		// Then
		checkHealthStatus(BACKEND_A, State.CLOSED);
	}

	@Test
	public void shouldOpenBackendBCircuitBreaker() {
		// When
		Stream.rangeClosed(1,4).forEach((count) -> produceFailure(BACKEND_B));

		// Then
		checkHealthStatus(BACKEND_B, State.OPEN);
	}

	@Test
	public void shouldCloseBackendBCircuitBreaker() {
		transitionToOpenState(BACKEND_B);
		circuitBreakerRegistry.circuitBreaker(BACKEND_B).transitionToHalfOpenState();

		// When
		Stream.rangeClosed(1,3).forEach((count) -> produceSuccess(BACKEND_B));

		// Then
		checkHealthStatus(BACKEND_B, State.CLOSED);
	}

	private void produceFailure(String backend) {
		ResponseEntity<String> response = restTemplate.getForEntity("/" + backend + "/failure", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void produceSuccess(String backend) {
		ResponseEntity<String> response = restTemplate.getForEntity("/" + backend + "/success", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}
