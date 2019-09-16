package io.github.robwin;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.collection.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static io.github.resilience4j.circuitbreaker.CircuitBreaker.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = Application.class)
@DirtiesContext
public class ReactiveCircuitBreakerTest {

	private static final String BACKEND_A = "backendA";
	private static final String BACKEND_B = "backendB";

	@Autowired
	private CircuitBreakerRegistry registry;

	@Autowired
	private WebTestClient webClient;

	@Test
	public void shouldOpenAndCloseBackendACircuitBreaker() throws InterruptedException {
		// When
		Stream.rangeClosed(1,5).forEach((count) -> produceFailure(BACKEND_A));

		// Then
		checkHealthStatus(BACKEND_A, State.OPEN);

		Thread.sleep(2000);

		// When
		Stream.rangeClosed(1,3).forEach((count) -> produceSuccess(BACKEND_A));

		checkHealthStatus(BACKEND_A, State.CLOSED);
	}

	@Test
	public void shouldOpenAndCloseBackendBCircuitBreaker() throws InterruptedException {
		// When
		Stream.rangeClosed(1,10).forEach((count) -> produceFailure(BACKEND_B));

		// Then
		checkHealthStatus(BACKEND_B, State.OPEN);

		Thread.sleep(2000);

		// When
		Stream.rangeClosed(1,3).forEach((count) -> produceSuccess(BACKEND_B));

		checkHealthStatus(BACKEND_B, State.CLOSED);
	}

	private void checkHealthStatus(String circuitBreakerName, State state) {
		CircuitBreaker circuitBreaker = registry.circuitBreaker(circuitBreakerName);
		assertThat(circuitBreaker.getState()).isEqualTo(state);
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
