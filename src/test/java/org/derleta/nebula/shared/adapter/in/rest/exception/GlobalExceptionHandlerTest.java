package org.derleta.nebula.shared.adapter.in.rest.exception;

import org.derleta.nebula.account.domain.exception.AccountNotFoundException;
import org.derleta.nebula.shared.adapter.in.rest.dto.ErrorResponse;
import org.derleta.nebula.shared.domain.exception.HttpRequestException;
import org.derleta.nebula.shared.domain.exception.MissingHeaderException;
import org.derleta.nebula.shared.domain.exception.TokenExpiredException;
import org.derleta.nebula.game.domain.exception.GameAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleTokenExpired() {
        String errorMessage = "ACCESS_TOKEN_EXPIRED";
        TokenExpiredException exception = new TokenExpiredException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTokenExpired(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(errorMessage, body.message());
        assertEquals("TOKEN_EXPIRED", body.error());
        assertNotNull(body.timestamp());
    }

    @Test
    void testHandleIllegalArgument() {
        String errorMessage = "Invalid argument";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgument(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(errorMessage, body.message());
        assertEquals("BAD_REQUEST", body.error());
        assertNotNull(body.timestamp());
    }

    @Test
    void testHandleMissingHeader() {
        String errorMessage = "Authorization header is missing";
        MissingHeaderException exception = new MissingHeaderException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgument(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(errorMessage, body.message());
        assertEquals("BAD_REQUEST", body.error());
        assertNotNull(body.timestamp());
    }

    @Test
    void testHandleNotFound() {
        String errorMessage = "Not found";
        RuntimeException exception = new RuntimeException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFound(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(errorMessage, body.message());
        assertEquals("NOT_FOUND", body.error());
        assertNotNull(body.timestamp());
    }

    @Test
    void testHandleAccountNotFound() {
        String errorMessage = "Account not found";
        AccountNotFoundException exception = new AccountNotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFound(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(errorMessage, body.message());
        assertEquals("NOT_FOUND", body.error());
        assertNotNull(body.timestamp());
    }

    @Test
    void testHandleAlreadyExists() {
        String errorMessage = "Already exists";
        GameAlreadyExistsException exception = new GameAlreadyExistsException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAlreadyExists(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(errorMessage, body.message());
        assertEquals("CONFLICT", body.error());
        assertNotNull(body.timestamp());
    }

    @Test
    void testHandleHttpRequest() {
        String errorMessage = "Failed to connect to external service";
        HttpRequestException exception = new HttpRequestException(errorMessage, new RuntimeException("Connection refused"));

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpRequest(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(errorMessage, body.message());
        assertEquals("BAD_GATEWAY", body.error());
        assertNotNull(body.timestamp());
    }
}

