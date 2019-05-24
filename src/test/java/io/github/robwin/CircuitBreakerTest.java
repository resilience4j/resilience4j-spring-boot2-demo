package io.github.robwin;

import io.vavr.collection.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = Application.class)
@DirtiesContext
public class CircuitBreakerTest {

	private static final String BACKEND_A = "backendA";
	private static final String BACKEND_B = "backendB";

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void shouldOpenAndCloseBackendACircuitBreaker() throws InterruptedException {
		// When
		Stream.rangeClosed(1,5).forEach((count) -> produceFailure(BACKEND_A));

		// Then
		checkHealthStatus(BACKEND_A + "CircuitBreaker", Status.DOWN);

		Thread.sleep(2000);

		// When
		Stream.rangeClosed(1,3).forEach((count) -> produceSuccess(BACKEND_A));

		checkHealthStatus(BACKEND_A + "CircuitBreaker", Status.UP);
	}

	@Test
	public void shouldOpenAndCloseBackendBCircuitBreaker() throws InterruptedException {
		// When
		Stream.rangeClosed(1,10).forEach((count) -> produceFailure(BACKEND_B));

		// Then
		checkHealthStatus(BACKEND_B + "CircuitBreaker", Status.DOWN);

		Thread.sleep(2000);

		// When
		Stream.rangeClosed(1,3).forEach((count) -> produceSuccess(BACKEND_B));

		checkHealthStatus(BACKEND_B + "CircuitBreaker", Status.UP);
	}

	private void checkHealthStatus(String circuitBreakerName, Status status) {
		ResponseEntity<HealthResponse> healthResponse = restTemplate.getForEntity("/actuator/health", HealthResponse.class);
		assertThat(healthResponse.getBody()).isNotNull();
		assertThat(healthResponse.getBody().getDetails()).isNotNull();
		Map<String, Object> backendACircuitBreakerDetails = healthResponse.getBody().getDetails().get(circuitBreakerName);
		assertThat(backendACircuitBreakerDetails).isNotNull();
		assertThat(backendACircuitBreakerDetails.get("status")).isEqualTo(status.toString());
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
