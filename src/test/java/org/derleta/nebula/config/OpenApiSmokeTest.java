package org.derleta.nebula.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class OpenApiSmokeTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void apiDocs_should_be_available() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/nebula/v3/api-docs",
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void swaggerUi_should_be_available() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/nebula/swagger-ui/index.html",
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Swagger UI"));
    }

    @Test
    void swaggerUi_should_be_available_ui() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/nebula/swagger-ui.html",
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Swagger UI"));
    }

    @Test
    void health_hello_should_be_available() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/v1/hello",
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("hello", response.getBody());
    }
}
