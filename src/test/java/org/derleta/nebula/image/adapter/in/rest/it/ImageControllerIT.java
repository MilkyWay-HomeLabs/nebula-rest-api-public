package org.derleta.nebula.image.adapter.in.rest.it;

import org.derleta.nebula.image.application.port.out.ImageStoragePort;
import org.derleta.nebula.shared.security.TokenProvider;
import org.derleta.nebula.testcontainer.FileServerTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ImageControllerIT {

    private static final String TEST_CSRF_TOKEN = "test-csrf-token";

    @Container
    static final FileServerTestContainer FILE_SERVER = new FileServerTestContainer();

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @MockitoBean
    TokenProvider tokenProvider;

    @MockitoBean
    ImageStoragePort imageStoragePort;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("nebula.fileserver.base-url", FILE_SERVER::getBaseUrl);
    }

    // ------------------------------------------------------------------ helpers

    private String url(String path) {
        return "http://localhost:" + port + "/api/v1/" + path;
    }

    private HttpEntity<MultiValueMap<String, Object>> buildRequest(String token, String resourcePath) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add(HttpHeaders.COOKIE, "accessToken=" + token + "; XSRF-TOKEN=" + TEST_CSRF_TOKEN);
        headers.add("X-XSRF-TOKEN", TEST_CSRF_TOKEN);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ClassPathResource(resourcePath));

        return new HttpEntity<>(body, headers);
    }

    @Test
    void fileserver_container_should_start() {
        assertTrue(FILE_SERVER.isRunning());
        assertTrue(FILE_SERVER.getBaseUrl().startsWith("http://"));
    }

    @Test
    void upload_valid_image_returns_201() {
        when(tokenProvider.isValid("valid-token")).thenReturn(true);
        when(tokenProvider.getUserId("valid-token")).thenReturn(42L);
        when(imageStoragePort.store(eq(42L), any())).thenReturn(true);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("image"),
                buildRequest("valid-token", "test/test-image.png"),
                String.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Image has been saved", response.getBody());
    }

    @Test
    void upload_invalid_token_returns_401() {
        when(tokenProvider.isValid("bad-token")).thenReturn(false);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("image"),
                buildRequest("bad-token", "test/test-image.png"),
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void upload_when_storage_fails_returns_403() {
        when(tokenProvider.isValid("valid-token")).thenReturn(true);
        when(tokenProvider.getUserId("valid-token")).thenReturn(42L);
        when(imageStoragePort.store(eq(42L), any())).thenReturn(false);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("image"),
                buildRequest("valid-token", "test/test-image.png"),
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Image has not been saved", response.getBody());
    }

    @Test
    void upload_unsupported_format_returns_400() {
        when(tokenProvider.isValid("valid-token")).thenReturn(true);
        when(tokenProvider.getUserId("valid-token")).thenReturn(42L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add(HttpHeaders.COOKIE, "accessToken=valid-token; XSRF-TOKEN=" + TEST_CSRF_TOKEN);
        headers.add("X-XSRF-TOKEN", TEST_CSRF_TOKEN);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ClassPathResource("test/index.html"));

        ResponseEntity<String> response = restTemplate.postForEntity(
                url("image"),
                new HttpEntity<>(body, headers),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

