package com.workhub.observability;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ObservabilityIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void healthEndpoint_isAvailable() {
        ResponseEntity<String> response = restTemplate.getForEntity(url("/actuator/health"), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    void livenessAndReadinessEndpoints_areAvailable() {
        ResponseEntity<String> liveness = restTemplate.getForEntity(url("/actuator/health/liveness"), String.class);
        ResponseEntity<String> readiness = restTemplate.getForEntity(url("/actuator/health/readiness"), String.class);

        assertThat(liveness.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(readiness.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(liveness.getBody()).contains("\"status\":\"UP\"");
        assertThat(readiness.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    void correlationId_isEchoedInResponseHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Correlation-ID", "it-corr-123");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url("/actuator/health"),
                HttpMethod.GET,
                entity,
                String.class
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getHeaders().getFirst("X-Correlation-ID")).isEqualTo("it-corr-123");
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
