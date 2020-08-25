package io.github.robwin;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import io.github.resilience4j.retry.Retry.Metrics;
import io.github.resilience4j.retry.RetryRegistry;

public abstract class AbstractRetryTest extends AbstractIntegrationTest {

    protected static final String FAILED_WITH_RETRY = "failed_with_retry";
    protected static final String SUCCESS_WITHOUT_RETRY = "successful_without_retry";

    @Autowired
    protected RetryRegistry retryRegistry;

    protected float getCurrentCount(String kind, String backend) {
        Metrics metrics = retryRegistry.retry(backend).getMetrics();

        if (FAILED_WITH_RETRY.equals(kind)) {
            return metrics.getNumberOfFailedCallsWithRetryAttempt();
        }
        if (SUCCESS_WITHOUT_RETRY.equals(kind)) {
            return metrics.getNumberOfSuccessfulCallsWithoutRetryAttempt();
        }

        return 0;
    }

    protected void checkMetrics(String kind, String backend, float count) {
        ResponseEntity<String> metricsResponse = restTemplate.getForEntity("/actuator/prometheus", String.class);
        assertThat(metricsResponse.getBody()).isNotNull();
        String response = metricsResponse.getBody();
        assertThat(response).contains(getMetricName(kind, backend) + count);
    }

    protected static String getMetricName(String kind, String backend) {
        return "resilience4j_retry_calls_total{application=\"resilience4j-demo\",kind=\"" + kind + "\",name=\"" + backend + "\",} ";
    }

}
