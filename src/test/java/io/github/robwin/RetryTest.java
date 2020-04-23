package io.github.robwin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class RetryTest extends AbstractRetryTest {

	@Test
	public void backendAshouldRetryThreeTimes() {
		// When
		float currentCount = getCurrentCount(FAILED_WITH_RETRY, BACKEND_A);
		produceFailure(BACKEND_A);

		checkMetrics(FAILED_WITH_RETRY, BACKEND_A, currentCount + 1);
	}

	@Test
	public void backendBshouldRetryThreeTimes() {
		// When
		float currentCount = getCurrentCount(FAILED_WITH_RETRY, BACKEND_B);
		produceFailure(BACKEND_B);

		checkMetrics(FAILED_WITH_RETRY, BACKEND_B, currentCount + 1);
	}

	@Test
	public void backendAshouldSucceedWithoutRetry() {
		float currentCount = getCurrentCount(SUCCESS_WITHOUT_RETRY, BACKEND_A);
		produceSuccess(BACKEND_A);

		checkMetrics(SUCCESS_WITHOUT_RETRY, BACKEND_A, currentCount + 1);
	}

	@Test
	public void backendBshouldSucceedWithoutRetry() {
		float currentCount = getCurrentCount(SUCCESS_WITHOUT_RETRY, BACKEND_B);
		produceSuccess(BACKEND_B);

		checkMetrics(SUCCESS_WITHOUT_RETRY, BACKEND_B, currentCount + 1);
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
