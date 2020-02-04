package io.github.robwin;

import io.vavr.collection.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static io.github.resilience4j.circuitbreaker.CircuitBreaker.State;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = Application.class)
@DirtiesContext
public class ReactiveCircuitBreakerTest extends AbstractCircuitBreakerTest{

	@Autowired
	private WebTestClient webClient;

	@Test
	public void shouldOpenBackendACircuitBreaker() {
		// When
		Stream.rangeClosed(1,2).forEach((count) -> produceFailure(BACKEND_A));

		// Then
		checkHealthStatus(BACKEND_A, State.OPEN);
	}

	@Test
	public void shouldOpenBackendBCircuitBreaker() {
		// When
		Stream.rangeClosed(1,4).forEach((count) -> produceFailure(BACKEND_B));

		// Then
		checkHealthStatus(BACKEND_B, State.OPEN);
	}

	@Test
	public void shouldCloseBackendACircuitBreaker() {
		transitionToOpenState(BACKEND_A);
		registry.circuitBreaker(BACKEND_A).transitionToHalfOpenState();

		// When
		Stream.rangeClosed(1,3).forEach((count) -> produceSuccess(BACKEND_A));

		// Then
		checkHealthStatus(BACKEND_A, State.CLOSED);
	}

	@Test
	public void shouldCloseBackendBCircuitBreaker() {
		transitionToOpenState(BACKEND_B);
		registry.circuitBreaker(BACKEND_B).transitionToHalfOpenState();

		// When
		Stream.rangeClosed(1,3).forEach((count) -> produceSuccess(BACKEND_B));

		// Then
		checkHealthStatus(BACKEND_B, State.CLOSED);
	}

	private void produceFailure(String backend) {
		webClient.get().uri("/" + backend + "/monoFailure").exchange().expectStatus()
				.is5xxServerError();
	}

	private void produceSuccess(String backend) {
		webClient.get().uri("/" + backend + "/monoSuccess").exchange().expectStatus()
				.isOk();
	}
}
