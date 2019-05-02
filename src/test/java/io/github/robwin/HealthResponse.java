package io.github.robwin;

import java.util.Map;

public class HealthResponse {
    private Map<String, Map<String, Object>> details;

    public Map<String, Map<String, Object>> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Map<String, Object>> details) {
        this.details = details;
    }
}
