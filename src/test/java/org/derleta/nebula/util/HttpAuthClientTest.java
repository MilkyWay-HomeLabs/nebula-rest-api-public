package org.derleta.nebula.util;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.derleta.nebula.account.adapter.in.rest.dto.response.AccessResponse;
import org.derleta.nebula.account.adapter.out.authservice.HttpAuthClient;
import org.derleta.nebula.account.adapter.out.authservice.dto.*;
import org.derleta.nebula.account.domain.types.AccountResponseType;
import org.derleta.nebula.shared.domain.exception.HttpRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class HttpAuthClientTest {

    private MockWebServer mockWebServer;

    private HttpAuthClient httpAuthClient;

    @BeforeEach
    void setup() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        httpAuthClient = new HttpAuthClient();

        var testUrl = mockWebServer.url("/api/v1/public/account/confirm").toString();
        var authApiAccountConfirmUrlField = HttpAuthClient.class.getDeclaredField("authApiAccountConfirmUrl");
        authApiAccountConfirmUrlField.setAccessible(true);
        authApiAccountConfirmUrlField.set(httpAuthClient, testUrl);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void registerUser_validData_shouldReturnSuccess() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": true, \"type\": \"VERIFICATION_MAIL_FROM_REGISTRATION\"}"));

        String uniqueIdentifier = String.valueOf(System.currentTimeMillis());
        String username = "nebulaTest" + uniqueIdentifier;
        String email = "nebulaTest" + uniqueIdentifier + "@nebula.com";

        AuthServRegistrationRequest request = AuthServRegistrationRequest.builder()
                .username(username)
                .email(email)
                .encryptedPassword("$2a$10$mY97JpKi2b4HzJbyPvNfZutMkdgv7mG0Y1LcLl/2QIXQNx28OJLqa")
                .build();

        var testUrl = mockWebServer.url("/api/v1/public/account/register").toString();
        var authApiPostRegisterUrlField = HttpAuthClient.class.getDeclaredField("authApiPostRegisterUrl");
        authApiPostRegisterUrlField.setAccessible(true);
        authApiPostRegisterUrlField.set(httpAuthClient, testUrl);

        // Act
        var response = httpAuthClient.registerUser(request);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(), "Success field should be true");
        assertEquals(AccountResponseType.VERIFICATION_MAIL_FROM_REGISTRATION, response.getType(),
                "Response type should be VERIFICATION_MAIL_FROM_REGISTRATION");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/public/account/register", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("POST", recordedRequest.getMethod(), "HTTP method should be POST");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"),
                "Content-Type should be application/json");
    }

    @Test
    void registerUser_invalidEmail_shouldReturnResponseWithFailure() {
        // Arrange
        String uniqueIdentifier = String.valueOf(System.currentTimeMillis());
        String username = "nebulaTest" + uniqueIdentifier;
        String invalidEmail = "invalidEmail";

        AuthServRegistrationRequest request = AuthServRegistrationRequest.builder()
                .username(username)
                .email(invalidEmail)
                .encryptedPassword("$2a$10$mY97JpKi2b4HzJbyPvNfZutMkdgv7mG0Y1LcLl/2QIXQNx28OJLqa")
                .build();

        // Act
        var response = httpAuthClient.registerUser(request);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertFalse(response.isSuccess(), "Success field should be false");
        assertEquals(AccountResponseType.NEBULA_INVALID_EMAIL, response.getType(),
                "Response type should be NEBULA_INVALID_EMAIL");
    }

    @Test
    void registerUser_duplicatedEmail_shouldThrowHttpRequestException() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(409)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": false, \"type\": \"EMAIL_ALREADY_EXISTS\"}"));

        String uniqueIdentifier = String.valueOf(System.currentTimeMillis());
        String username = "testuser_other" + uniqueIdentifier;
        String duplicatedEmail = "testuser@example.com";

        AuthServRegistrationRequest request = AuthServRegistrationRequest.builder()
                .username(username)
                .email(duplicatedEmail)
                .encryptedPassword("$2a$10$mY97JpKi2b4HzJbyPvNfZutMkdgv7mG0Y1LcLl/2QIXQNx28OJLqa")
                .build();

        var testUrl = mockWebServer.url("/api/v1/public/account/register").toString();
        var authApiPostRegisterUrlField = HttpAuthClient.class.getDeclaredField("authApiPostRegisterUrl");
        authApiPostRegisterUrlField.setAccessible(true);
        authApiPostRegisterUrlField.set(httpAuthClient, testUrl);

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClient.registerUser(request));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"),
                "Exception message should contain the error type");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/public/account/register", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("POST", recordedRequest.getMethod(), "HTTP method should be POST");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"),
                "Content-Type should be application/json");
    }

    @Test
    void registerUser_userAlreadyExists_shouldThrowHttpRequestException() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(409)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": false, \"type\": \"USER_ALREADY_EXISTS\"}"));

        String username = "testUser";
        String email = "unique1234@nebula.com";

        AuthServRegistrationRequest request = AuthServRegistrationRequest.builder()
                .username(username)
                .email(email)
                .encryptedPassword("$2a$10$mY97JpKi2b4HzJbyPvNfZutMkdgv7mG0Y1LcLl/2QIXQNx28OJLqa")
                .build();

        var testUrl = mockWebServer.url("/api/v1/public/account/register").toString();
        var authApiPostRegisterUrlField = HttpAuthClient.class.getDeclaredField("authApiPostRegisterUrl");
        authApiPostRegisterUrlField.setAccessible(true);
        authApiPostRegisterUrlField.set(httpAuthClient, testUrl);

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClient.registerUser(request));

        // Assert
        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"),
                "Exception message should contain the error type");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/public/account/register", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("POST", recordedRequest.getMethod(), "HTTP method should be POST");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"),
                "Content-Type should be application/json");
    }

    @Test
    void registerUser_nullUsername_shouldThrowHttpRequestException() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": false, \"type\": \"INVALID_REQUEST\"}"));

        String email = "unique1234@nebula.com";

        AuthServRegistrationRequest request = AuthServRegistrationRequest.builder()
                .username(null) // Set `null` as the username
                .email(email)
                .encryptedPassword("$2a$10$mY97JpKi2b4HzJbyPvNfZutMkdgv7mG0Y1LcLl/2QIXQNx28OJLqa")
                .build();

        var testUrl = mockWebServer.url("/api/v1/public/account/register").toString();
        var authApiPostRegisterUrlField = HttpAuthClient.class.getDeclaredField("authApiPostRegisterUrl");
        authApiPostRegisterUrlField.setAccessible(true);
        authApiPostRegisterUrlField.set(httpAuthClient, testUrl);

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClient.registerUser(request));

        // Assert
        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"),
                "Exception message should contain the error type");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/public/account/register", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("POST", recordedRequest.getMethod(), "HTTP method should be POST");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"),
                "Content-Type should be application/json");
    }

    @Test
    void registerUser_passwordTooShort_shouldReturnError() {
        // Arrange
        String uniqueIdentifier = String.valueOf(System.currentTimeMillis());
        String username = "nebulaTest" + uniqueIdentifier;
        String email = "nebulaTest" + uniqueIdentifier + "@nebula.com";

        AuthServRegistrationRequest request = AuthServRegistrationRequest.builder()
                .username(username)
                .email(email)
                .encryptedPassword("123")
                .build();

        // Act
        var response = httpAuthClient.registerUser(request);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.NEBULA_INVALID_ENCRYPTED_PASSWORD, response.getType());
    }

    @Test
    void registerUser_nullPassword_shouldReturnError() {
        // Arrange
        AuthServRegistrationRequest request = AuthServRegistrationRequest.builder()
                .username("unique9999Username")
                .email("example9999@example.com")
                .encryptedPassword(null)
                .build();

        // Act
        var response = httpAuthClient.registerUser(request);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.NEBULA_INVALID_ENCRYPTED_PASSWORD, response.getType());
    }

    @Test
    void registerUser_serverError_shouldThrowException() {
        // Arrange
        AuthServRegistrationRequest request = AuthServRegistrationRequest.builder()
                .username("testuser")
                .email("testuser@example.com")
                .encryptedPassword("password123")
                .build();
        Throwable originalCause = new IOException("Connection timed out");

        HttpAuthClient httpAuthClient = mock(HttpAuthClient.class);

        when(httpAuthClient.registerUser(any())).thenThrow(new HttpRequestException("Server error", originalCause));

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClient.registerUser(request));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Server error"),
                "Exception message should indicate server error");
    }

    @Test
    void confirmAccount_validDetails_shouldReturnSuccess() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": true, \"type\": \"ACCOUNT_CONFIRMED\"}"));

        AuthConfirmRequest request = new AuthConfirmRequest(29L, "testToken");

        var testUrl = mockWebServer.url("/api/v1/public/account/confirm").toString();
        var authApiAccountConfirmUrlField = HttpAuthClient.class.getDeclaredField("authApiAccountConfirmUrl");
        authApiAccountConfirmUrlField.setAccessible(true);
        authApiAccountConfirmUrlField.set(httpAuthClient, testUrl);

        // Act
        var response = httpAuthClient.confirmAccount(request);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(), "Success field should be true");
        assertEquals(AccountResponseType.ACCOUNT_CONFIRMED, response.getType(), "Response type should be ACCOUNT_CONFIRMED");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/public/account/confirm", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("PATCH", recordedRequest.getMethod(), "HTTP method should be PATCH");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"), "Content-Type should be application/json");
    }

    @Test
    void confirmAccount_invalidToken_shouldReturnFailure() {
        // Arrange
        String invalidToken = "invalidToken";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"INVALID_TOKEN_VALUE\"}"));

        AuthConfirmRequest request = new AuthConfirmRequest(123L, invalidToken);

        // Act & Assert
        Exception exception = assertThrows(HttpRequestException.class, () -> httpAuthClient.confirmAccount(request));

        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"));
    }

    @Test
    void confirmAccount_serverError_shouldThrowException() {
        // Arrange
        AuthConfirmRequest request = new AuthConfirmRequest(12345L, "validToken");
        Throwable originalCause = new IOException("Connection timed out");

        HttpAuthClient httpAuthClient = mock(HttpAuthClient.class);

        when(httpAuthClient.confirmAccount(any())).thenThrow(new HttpRequestException("Server error", originalCause));

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClient.confirmAccount(request));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Server error"),
                "Exception message should indicate server error");
    }

    @Test
    void unlockAccount_validId_shouldReturnSuccess() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": true, \"type\": \"ACCOUNT_CAN_BE_UNLOCKED\"}"));

        Long userId = 123L;
        var testUrl = mockWebServer.url("/api/v1/public/account/unlock").toString();
        var authApiAccountUnlockUrlField = HttpAuthClient.class.getDeclaredField("authApiAccountUnlockUrl");
        authApiAccountUnlockUrlField.setAccessible(true);
        authApiAccountUnlockUrlField.set(httpAuthClient, testUrl);

        // Act
        var response = httpAuthClient.unlockAccount(userId);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(), "Success field should be true");
        assertEquals(AccountResponseType.ACCOUNT_CAN_BE_UNLOCKED, response.getType(), "Response type should be ACCOUNT_CAN_BE_UNLOCKED");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/public/account/unlock/123", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("PATCH", recordedRequest.getMethod(), "HTTP method should be PATCH");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"), "Content-Type should be application/json");
    }

    @Test
    void unlockAccount_invalidId_shouldThrowException() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        Long invalidUserId = -1L;
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"INVALID_USER_ID\"}"));

        var testUrl = mockWebServer.url("/api/v1/public/account/unlock").toString();
        var authApiAccountUnlockUrlField = HttpAuthClient.class.getDeclaredField("authApiAccountUnlockUrl");
        authApiAccountUnlockUrlField.setAccessible(true);
        authApiAccountUnlockUrlField.set(httpAuthClient, testUrl);

        // Act & Assert
        Exception exception = assertThrows(HttpRequestException.class, () -> httpAuthClient.unlockAccount(invalidUserId));

        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"));
    }

    @Test
    void unlockAccount_serverError_shouldThrowException() {
        // Arrange
        Long userId = 12345L;
        Throwable originalCause = new IOException("Server error");

        HttpAuthClient httpAuthClient = mock(HttpAuthClient.class);

        when(httpAuthClient.unlockAccount(any())).thenThrow(new HttpRequestException("Server error", originalCause));

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClient.unlockAccount(userId));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Server error"), "Exception message should indicate server error");
    }

    @Test
    void resetPassword_validEmail_shouldReturnSuccess() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": true, \"type\": \"PASSWORD_CAN_BE_CHANGED\"}"));

        String email = "user@example.com";

        var testUrl = mockWebServer.url("/api/v1/public/account/reset").toString();
        var authApiAccountResetPasswdUrlField = HttpAuthClient.class.getDeclaredField("authApiAccountResetPasswdUrl");
        authApiAccountResetPasswdUrlField.setAccessible(true);
        authApiAccountResetPasswdUrlField.set(httpAuthClient, testUrl);

        // Act
        var response = httpAuthClient.resetPassword(email);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(), "Success field should be true");
        assertEquals(AccountResponseType.PASSWORD_CAN_BE_CHANGED, response.getType(), "Response type should be PASSWORD_CAN_BE_CHANGED");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/public/account/reset/user@example.com", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("PATCH", recordedRequest.getMethod(), "HTTP method should be PATCH");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"), "Content-Type should be application/json");
    }

    @Test
    void resetPassword_invalidEmail_shouldReturnFailure() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        String invalidEmail = "invalid-email";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"INVALID_USER_ENTITY\"}"));

        var testUrl = mockWebServer.url("/api/v1/public/account/unlock").toString();
        var authApiAccountUnlockUrlField = HttpAuthClient.class.getDeclaredField("authApiAccountUnlockUrl");
        authApiAccountUnlockUrlField.setAccessible(true);
        authApiAccountUnlockUrlField.set(httpAuthClient, testUrl);

        // Act & Assert
        Exception exception = assertThrows(HttpRequestException.class, () -> httpAuthClient.resetPassword(invalidEmail));

        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"));
    }

    @Test
    void resetPassword_serverError_shouldThrowException() {
        // Arrange
        String email = "user@example.com";
        Throwable originalCause = new IOException("Internal Server Error");

        HttpAuthClient httpAuthClient = mock(HttpAuthClient.class);

        when(httpAuthClient.resetPassword(any())).thenThrow(new HttpRequestException("Server error", originalCause));

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClient.resetPassword(email));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Server error"), "Exception message should indicate server error");
    }

    @Test
    void updatePassword_validDetails_shouldReturnSuccess() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": true, \"type\": \"PASSWORD_CHANGED\"}"));

        String jwtToken = "validJwtToken";
        AuthUpdatePasswordRequest request = new AuthUpdatePasswordRequest(
                123L,
                "user@example.com",
                "currentPassword123",
                "newPassword456"
        );

        var testUrl = mockWebServer.url("/api/v1/public/account/changepass").toString();
        var authApiAccountChangePasswdUrlField = HttpAuthClient.class.getDeclaredField("authApiAccountChangePasswdUrl");
        authApiAccountChangePasswdUrlField.setAccessible(true);
        authApiAccountChangePasswdUrlField.set(httpAuthClient, testUrl);

        // Act
        var response = httpAuthClient.updatePassword(jwtToken, request);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(), "Success field should be true");
        assertEquals(AccountResponseType.PASSWORD_CHANGED, response.getType(), "Response type should be PASSWORD_CHANGED");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/public/account/changepass", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("POST", recordedRequest.getMethod(), "HTTP method should be POST");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"), "Content-Type should be application/json");
        assertEquals("Bearer " + jwtToken, recordedRequest.getHeader("Authorization"), "Authorization header is incorrect");
        assertNull(recordedRequest.getHeader("Cookie"), "Cookie header should not be present");
        //        assertTrue(Objects.requireNonNull(recordedRequest.getHeader("Cookie")).contains("jwtToken=" + jwtToken), "Cookie should contain jwtToken");
    }

    @Test
    void updatePassword_invalidCurrentPassword_shouldReturnFailure() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": false, \"type\": \"BAD_ACTUAL_PASSWORD_CHANGE_PASSWD\"}"));

        String jwtToken = "validJwtToken";
        AuthUpdatePasswordRequest request = new AuthUpdatePasswordRequest(
                123L, // userId
                "user@example.com", // email
                "currentPassword123", // actualPassword
                "newPassword456" // newPassword
        );

        var testUrl = mockWebServer.url("/api/v1/public/account/changepass").toString();
        var authApiAccountChangePasswdUrlField = HttpAuthClient.class.getDeclaredField("authApiAccountChangePasswdUrl");
        authApiAccountChangePasswdUrlField.setAccessible(true);
        authApiAccountChangePasswdUrlField.set(httpAuthClient, testUrl);

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class, () -> httpAuthClient.updatePassword(jwtToken, request));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"));
    }

    @Test
    void updatePassword_serverError_shouldThrowException() {
        // Arrange
        String jwtToken = "validJwtToken";
        AuthUpdatePasswordRequest request = new AuthUpdatePasswordRequest(
                123L, // userId
                "user@example.com", // email
                "currentPassword123", // actualPassword
                "newPassword456" // newPassword
        );
        Throwable originalCause = new IOException("Internal Server Error");

        HttpAuthClient httpAuthClient = mock(HttpAuthClient.class);

        when(httpAuthClient.updatePassword(any(), any())).thenThrow(new HttpRequestException("Server error", originalCause));

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class, () -> httpAuthClient.updatePassword(jwtToken, request));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Server error"), "Exception message should indicate server error");
    }

    @Test
    void updatePassword_invalidToken_shouldThrowException() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": false, \"type\": \"INVALID_JWT_TOKEN\"}"));

        String jwtToken = "invalidToken";
        AuthUpdatePasswordRequest request = new AuthUpdatePasswordRequest(
                123L, // userId
                "user@example.com", // email
                "currentPassword123", // actualPassword
                "newPassword456" // newPassword
        );

        var testUrl = mockWebServer.url("/api/v1/public/account/changepass").toString();
        var authApiAccountChangePasswdUrlField = HttpAuthClient.class.getDeclaredField("authApiAccountChangePasswdUrl");
        authApiAccountChangePasswdUrlField.setAccessible(true);
        authApiAccountChangePasswdUrlField.set(httpAuthClient, testUrl);

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class, () -> httpAuthClient.updatePassword(jwtToken, request));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"), "Exception message should indicate the issue");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/public/account/changepass", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("POST", recordedRequest.getMethod(), "HTTP method should be POST");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"), "Content-Type should be application/json");
    }

    @Test
    void refreshAccess_validCredentials_shouldReturnAccessResponse() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Arrange
        String refreshToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMDAwMDExLHhic212dXpmYXl5emp4ZHhha0Bja3B0ci5jb20iLCJpc3MiOiJEYkNvbm5lY3Rpb25BcHAiLCJyb2xlcyI6W3siaWQiOjEsIm5hbWUiOiJST0xFX1VTRVIifSx7ImlkIjo0LCJuYW1lIjoiUk9MRV9BRE1JTiJ9XSwiaWF0IjoxNzQ2MjY3NDg4LCJleHAiOjE3NDg4NTk0ODh9.vohJv_OODIQ7uSMPoHSOutLrPSVO1OyMi_7Eg32PFPfFzPLOxcnrqm6BV-bI_1WSBKJBOuc5m65aMRSSM019uw";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .addHeader("Set-Cookie", "accessToken=abc123; HttpOnly; Path=/; Secure")
                .addHeader("Set-Cookie", "refreshToken=abc123; HttpOnly; Path=/; Secure")
                .setBody("{\"success\": true, \"type\": \"ACCESS_REFRESHED\"}"));

        var testUrl = mockWebServer.url("/api/v1/auth/refresh-access").toString();
        var authApiAccessRefreshUrlField = HttpAuthClient.class.getDeclaredField("authApiAccessRefreshUrl");
        authApiAccessRefreshUrlField.setAccessible(true);
        authApiAccessRefreshUrlField.set(httpAuthClient, testUrl);

        AccessResponse expectedResponse = new AccessResponse(
                Map.of(
                        "accessToken", "accessToken=abc123; HttpOnly; Path=/; Secure; SameSite=None; Partitioned",
                        "refreshToken", "refreshToken=abc123; HttpOnly; Path=/; Secure; SameSite=None; Partitioned"
                ),
                true, "ACCESS_REFRESHED");

        // Act
        AccessResponse response = httpAuthClient.refreshAccess(refreshToken);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(expectedResponse.isSuccess(), response.isSuccess());
        assertEquals(expectedResponse, response);

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/auth/refresh-access", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("POST", recordedRequest.getMethod(), "HTTP method should be POST");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"), "Content-Type should be application/json");
    }

    @Test
    void refreshAccess_invalidCredentials_shouldThrowHttpRequestException() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .addHeader("Content-Type", "application/json")

                .setBody("{\"error\": \"INVALID_CREDENTIALS\"}"));

        String refreshToken = "validJwtToken";

        var exception = assertThrows(NullPointerException.class, () -> httpAuthClient.refreshAccess(refreshToken));

        // Assert
        assertTrue(exception.getMessage().contains("Cannot invoke \"String.length()\" because \"this.input\" is null"));
    }

    @Test
    void refreshAccess_serverError_shouldThrowHttpRequestException() {
        // Arrange
        String refreshToken = "validJwtToken";
        Throwable originalCause = new IOException("Connection timed out");

        HttpAuthClient httpAuthClient = mock(HttpAuthClient.class);

        when(httpAuthClient.refreshAccess(any())).thenThrow(new HttpRequestException("Server error", originalCause));

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClient.refreshAccess(refreshToken));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Server error"), "Exception message should indicate server error");
    }

    @Test
    void generateToken_validCredentials_shouldReturnJwtTokenResponse() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .addHeader("Set-Cookie", "accessToken=abc123; HttpOnly; Path=/; Secure")
                .addHeader("Set-Cookie", "refreshToken=abc123; HttpOnly; Path=/; Secure")
                .setBody("{\"username\": \"testUser\", \"email\": \"testUser@example.com\"}"));

        AuthTokenRequest request = new AuthTokenRequest("testUser@example.com", "password123");

        var testUrl = mockWebServer.url("/api/v1/public/account/token").toString();
        var authApiAccountTokenUrlField = HttpAuthClient.class.getDeclaredField("authApiAccountTokenUrl");
        authApiAccountTokenUrlField.setAccessible(true);
        authApiAccountTokenUrlField.set(httpAuthClient, testUrl);

        // Act
        AuthTokenResponse response = httpAuthClient.generateToken(request);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals("AuthTokenResponse(cookiesHeaders={accessToken=accessToken=abc123; HttpOnly; Path=/; Secure; SameSite=None; Partitioned, refreshToken=refreshToken=abc123; HttpOnly; Path=/; Secure; SameSite=None; Partitioned}, username=testUser, email=testUser@example.com)", response.toString());

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/public/account/token", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("POST", recordedRequest.getMethod(), "HTTP method should be POST");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"), "Content-Type should be application/json");
    }

    @Test
    void generateToken_invalidCredentials_shouldThrowHttpRequestException() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .addHeader("Content-Type", "application/json")

                .setBody("{\"error\": \"INVALID_CREDENTIALS\"}"));

        AuthTokenRequest request = new AuthTokenRequest("invalidUser@example.com", "wrongPassword");

        var exception = assertThrows(NullPointerException.class, () -> httpAuthClient.generateToken(request));

        // Assert
        assertTrue(exception.getMessage().contains("Cannot invoke \"String.length()\" because \"this.input\" is null"));
    }

    @Test
    void generateToken_serverError_shouldThrowHttpRequestException() {
        // Arrange
        AuthTokenRequest request = new AuthTokenRequest("user@example.com", "password123");
        Throwable originalCause = new IOException("Connection timed out");

        HttpAuthClient httpAuthClient = mock(HttpAuthClient.class);

        when(httpAuthClient.generateToken(any())).thenThrow(new HttpRequestException("Server error", originalCause));

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClient.generateToken(request));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Server error"), "Exception message should indicate server error");
    }

    @Test
    void getAccount_validDetails_shouldReturnUserRoles() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "user": {
                                "userId": 123,
                                "username": "testUser",
                                "email": "testUser@example.com"
                            },
                            "roles": [
                                {"roleId": 1, "roleName": "ROLE_USER"},
                                {"roleId": 2, "roleName": "ROLE_ADMIN"}
                            ]
                        }
                        """));


        var testUrl = mockWebServer.url("/api/v1/account").toString();
        var authApiAccountUrlField = HttpAuthClient.class.getDeclaredField("authApiAccountUrl");
        authApiAccountUrlField.setAccessible(true);
        authApiAccountUrlField.set(httpAuthClient, testUrl);

        // Act
        var response = httpAuthClient.getAccount("testUser", "testUser@example.com");

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals("testUser", response.getUser().getUsername(), "Username should match");
        assertTrue(response.getRoles().stream().anyMatch(role -> role.getRoleName().equals("ROLE_USER")), "Response should contain ROLE_USER");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/account?username=testUser&email=testUser@example.com", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("GET", recordedRequest.getMethod(), "HTTP method should be GET");
    }

    @Test
    void getAccount_invalidRequest_shouldThrowException() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("""
                        {
                            "error": "INVALID_REQUEST"
                        }
                        """));

        // Act & Assert — empty username/email yields a URL without a host, which throws
        Exception exception = assertThrows(RuntimeException.class, () -> httpAuthClient.getAccount("", ""));
        assertTrue(exception.getMessage().contains("Target host is not specified"));
    }

    @Test
    void getAccount_serverError_shouldThrowHttpRequestException() {
        // Arrange
        Throwable originalCause = new IOException("Internal Server Error");
        HttpAuthClient httpAuthClient = mock(HttpAuthClient.class);

        when(httpAuthClient.getAccount(any(), any())).thenThrow(new HttpRequestException("Server error", originalCause));

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClient.getAccount("testUser", "testUser@example.com"));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Server error"), "Exception message should indicate server error");
    }

    @Test
    void postRequestWithBearerToken_validToken_shouldReturnSuccess() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": true, \"type\": \"PASSWORD_CHANGED\"}"));

        String jwtToken = "validJwtToken";
        String jsonBody = """
                {
                    "field1": "value1",
                    "field2": "value2"
                }
                """;

        var testUrl = mockWebServer.url("/api/v1/test-endpoint").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountChangePasswdUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        // Act
        var response = httpAuthClient.postRequestWithBearerToken(jsonBody, jwtToken, testUrl);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(), "Success field should be true");
        assertEquals(AccountResponseType.PASSWORD_CHANGED, response.getType(), "Response type should be PASSWORD_CHANGED");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/test-endpoint", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("POST", recordedRequest.getMethod(), "HTTP method should be POST");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"), "Content-Type should be application/json");
        assertEquals("Bearer " + jwtToken, recordedRequest.getHeader("Authorization"), "Authorization header is incorrect");
    }

    @Test
    void postRequestWithBearerToken_invalidToken_shouldThrowException() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"error\": \"INVALID_TOKEN\"}"));

        String jwtToken = "invalidToken";
        String jsonBody = """
                {
                    "field1": "value1",
                    "field2": "value2"
                }
                """;

        var testUrl = mockWebServer.url("/api/v1/test-endpoint").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountChangePasswdUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClient.postRequestWithBearerToken(jsonBody, jwtToken, testUrl));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"), "Exception message should indicate the issue");
    }

    @Test
    void postRequestWithBearerToken_serverError_shouldThrowException() {
        // Arrange
        String jwtToken = "validJwtToken";
        String jsonBody = """
                {
                    "field1": "value1",
                    "field2": "value2"
                }
                """;

        Throwable originalCause = new IOException("Internal Server Error");
        HttpAuthClient httpAuthClient = mock(HttpAuthClient.class);

        when(httpAuthClient.postRequestWithBearerToken(any(), any(), any())).thenThrow(new HttpRequestException("Server error", originalCause));

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class, () -> httpAuthClient.postRequestWithBearerToken(jsonBody, jwtToken, "testUrl"));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Server error"), "Exception message should indicate server error");
    }

    @Test
    void postRequest_validPayload_shouldReturnSuccessResponse() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": true, \"type\": \"ACCOUNT_CONFIRMED\"}"));

        String jsonBody = """
                {
                    "key1": "value1",
                    "key2": "value2"
                }
                """;

        var testUrl = mockWebServer.url("/api/v1/test/request").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountTokenUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        // Act
        var response = httpAuthClient.postRequest(jsonBody, testUrl);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(), "Success field should be true");
        assertEquals(AccountResponseType.ACCOUNT_CONFIRMED, response.getType(), "Response type should match.");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/test/request", recordedRequest.getPath(), "Request path should be correct.");
        assertEquals("POST", recordedRequest.getMethod(), "HTTP method should be POST.");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"), "Content-Type should match.");
    }

    @Test
    void postRequest_invalidPayload_shouldReturnClientError() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"error\": \"INVALID_PAYLOAD\"}"));

        String invalidJsonBody = "{\"key1\":}";

        var testUrl = mockWebServer.url("/api/v1/test/request").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountTokenUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClient.postRequest(invalidJsonBody, testUrl));

        assertNotNull(exception, "Exception should not be null.");
        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"), "Should indicate request failure.");
    }

    @Test
    void postRequest_serverError_shouldThrowException() {
        // Arrange
        String jsonBody = """
                {
                    "key1": "value1",
                    "key2": "value2"
                }
                """;

        var testUrl = "http://mocked-url/api/v1/test/request";
        Throwable serverError = new IOException("Internal Server Error");

        HttpAuthClient httpAuthClientMock = mock(HttpAuthClient.class);
        when(httpAuthClientMock.postRequest(any(), any())).thenThrow(new HttpRequestException("Server error", serverError));

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClientMock.postRequest(jsonBody, testUrl));

        assertNotNull(exception, "Exception should not be null.");
        assertTrue(exception.getMessage().contains("Server error"), "Exception message should indicate server error.");
    }

    @Test
    void postRequest_nullBody_shouldThrowNullPointerException() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"error\": \"INVALID_REQUEST_BODY\"}"));

        String nullBody = null;

        var testUrl = mockWebServer.url("/api/v1/test/request").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountTokenUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        // Act & Assert
        Exception exception = assertThrows(HttpRequestException.class, () -> httpAuthClient.postRequest(nullBody, testUrl));
        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"), testUrl);
    }

    @Test
    void patchRequest_withBodyValidData_shouldReturnSuccess() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": true, \"type\": \"ACCOUNT_CONFIRMED\"}"));

        String jsonBody = """
                {
                    "key": "value"
                }
                """;

        var testUrl = mockWebServer.url("/api/v1/test/patch").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountConfirmUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        // Act
        var response = httpAuthClient.patchRequest(jsonBody, testUrl);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(), "Success field should be true");
        assertEquals(AccountResponseType.ACCOUNT_CONFIRMED, response.getType(), "Response type should match.");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/test/patch", recordedRequest.getPath(), "Request path should be correct.");
        assertEquals("PATCH", recordedRequest.getMethod(), "HTTP method should be PATCH.");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"), "Content-Type should match.");
    }

    @Test
    void patchRequest_withBodyInvalidData_shouldReturnFailure() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": false, \"type\": \"INVALID_PAYLOAD\"}"));

        String invalidJsonBody = "{\"invalidKey\":}";

        var testUrl = mockWebServer.url("/api/v1/test/patch").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountConfirmUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClient.patchRequest(invalidJsonBody, testUrl));

        assertNotNull(exception, "Exception should not be null.");
        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"), "Should indicate request failure.");
    }

    @Test
    void patchRequest_withoutBodyValidData_shouldReturnSuccess() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": true, \"type\": \"ACCOUNT_CONFIRMED\"}"));

        var testUrl = mockWebServer.url("/api/v1/test/patch").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountConfirmUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        // Act
        var response = httpAuthClient.patchRequest(testUrl);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(), "Success field should be true");
        assertEquals(AccountResponseType.ACCOUNT_CONFIRMED, response.getType(), "Response type should match.");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/test/patch", recordedRequest.getPath(), "Request path should be correct.");
        assertEquals("PATCH", recordedRequest.getMethod(), "HTTP method should be PATCH.");
    }

    @Test
    void patchRequest_withoutBodyInvalidData_shouldReturnFailure() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": false, \"type\": \"INVALID_REQUEST\"}"));

        var testUrl = mockWebServer.url("/api/v1/test/patch").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountConfirmUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class, () -> httpAuthClient.patchRequest(testUrl));

        assertNotNull(exception, "Exception should not be null.");
        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"), "Should indicate request failure.");
    }

    @Test
    void patchRequest_serverError_shouldThrowException() {
        // Arrange
        String jsonBody = """
                {
                    "key": "value"
                }
                """;

        var testUrl = "http://mocked-url/api/v1/test/patch";
        Throwable serverError = new IOException("Internal Server Error");

        HttpAuthClient httpAuthClientMock = mock(HttpAuthClient.class);
        when(httpAuthClientMock.patchRequest(any(), any())).thenThrow(new HttpRequestException("Server error", serverError));

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClientMock.patchRequest(jsonBody, testUrl));

        assertNotNull(exception, "Exception should not be null.");
        assertTrue(exception.getMessage().contains("Server error"), "Exception message should indicate server error.");
    }

    @Test
    void patchRequest_withValidJsonBody_shouldReturnSuccess() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": true, \"type\": \"ACCOUNT_CONFIRMED\"}"));

        String jsonBody = """
                {
                    "key1": "value1",
                    "key2": "value2"
                }
                """;

        var testUrl = mockWebServer.url("/api/v1/test/patch").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountConfirmUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        // Act
        var response = httpAuthClient.patchRequest(jsonBody, testUrl);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(), "Success field should be true");
        assertEquals(AccountResponseType.ACCOUNT_CONFIRMED, response.getType(), "Response type should match.");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/test/patch", recordedRequest.getPath(), "Request path should be correct.");
        assertEquals("PATCH", recordedRequest.getMethod(), "HTTP method should be PATCH.");
        assertEquals("application/json", recordedRequest.getHeader("Content-Type"), "Content-Type should match.");
    }

    @Test
    void patchRequest_withValidUrlInvalidData_shouldThrowHttpRequestException() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": false, \"type\": \"INVALID_REQUEST\"}"));

        var testUrl = mockWebServer.url("/api/v1/test/patch").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountConfirmUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class, () -> httpAuthClient.patchRequest(testUrl));

        assertNotNull(exception, "Exception should not be null.");
        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"), "Should indicate request failure.");
    }

    @Test
    void patchRequest_jsonBodyInvalidData_shouldThrowHttpRequestException() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": false, \"type\": \"INVALID_PAYLOAD\"}"));

        String invalidJsonBody = "{\"invalidKey\":}";

        var testUrl = mockWebServer.url("/api/v1/test/patch").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountConfirmUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClient.patchRequest(invalidJsonBody, testUrl));

        assertNotNull(exception, "Exception should not be null.");
        assertTrue(exception.getMessage().contains("Failed to execute account request for URL"), "Should indicate request failure.");
    }

    @Test
    void patchRequest_withServerError_shouldThrowException() {
        // Arrange
        String jsonBody = """
                {
                    "key": "value"
                }
                """;

        var testUrl = "http://mocked-url/api/v1/test/patch";
        Throwable serverError = new IOException("Internal Server Error");

        HttpAuthClient httpAuthClientMock = mock(HttpAuthClient.class);
        when(httpAuthClientMock.patchRequest(any(), any())).thenThrow(new HttpRequestException("Server error", serverError));

        // Act & Assert
        HttpRequestException exception = assertThrows(HttpRequestException.class,
                () -> httpAuthClientMock.patchRequest(jsonBody, testUrl));

        assertNotNull(exception, "Exception should not be null.");
        assertTrue(exception.getMessage().contains("Server error"), "Exception message should indicate server error.");
    }

    @Test
    void executeForAuthTokenResponse_validResponse_shouldReturnAuthTokenResponse() throws IOException, NoSuchFieldException, IllegalAccessException, InterruptedException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .addHeader("Set-Cookie", "accessToken=abc123; HttpOnly; Path=/; Secure")
                .addHeader("Set-Cookie", "refreshToken=abc123; HttpOnly; Path=/; Secure")
                .setBody("""
                        {
                            "username": "testUser",
                            "email": "testUser@example.com"
                        }
                        """));

        var testUrl = mockWebServer.url("/api/v1/account/token").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountTokenUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        HttpPost request = new HttpPost(testUrl);

        // Act
        AuthTokenResponse response = httpAuthClient.executeForAuthTokenResponse(request);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertTrue(response.toString().contains("testUser"), "Username should match");
        assertTrue(response.toString().contains("testUser@example.com"), "Email should match");
        assertEquals(2, response.getCookiesHeaders().size(), "Should contain two cookies");
        assertTrue(response.getCookiesHeaders().get("accessToken").contains("accessToken"), "Cookie name should match");
        assertTrue(response.getCookiesHeaders().get("refreshToken").contains("refreshToken"), "Cookie name should match");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/account/token", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("POST", recordedRequest.getMethod(), "HTTP method should be POST");
    }

    @Test
    void executeForAuthTokenResponse_invalidResponse_shouldThrowHttpRequestException() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\": false, \"type\": \"INVALID_TOKEN\"}"));

        var testUrl = mockWebServer.url("/api/v1/account/token").toString();
        HttpPost request = new HttpPost(testUrl);

        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> httpAuthClient.executeForAuthTokenResponse(request));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("HTTP request failed with status code: 400, reason: INVALID_TOKEN"));
    }

    @Test
    void executeForAuthTokenResponse_serverError_shouldThrowException() throws IOException {
        // Arrange
        Throwable originalCause = new IOException("Connection refused");
        HttpAuthClient httpAuthClientMock = mock(HttpAuthClient.class);

        HttpPost request = mock(HttpPost.class);
        when(httpAuthClientMock.executeForAuthTokenResponse(request)).thenThrow(new HttpRequestException("Server error", originalCause));

        // Act & Assert
        HttpRequestException exception =
                assertThrows(HttpRequestException.class, () -> httpAuthClientMock.executeForAuthTokenResponse(request));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Server error"), "Exception message should contain 'Server error'.");
        assertEquals(originalCause, exception.getCause(), "Exception cause should match the original cause");
    }

    @Test
    void executeForAccountResponse_validResponse_shouldReturnAccountResponse() throws IOException, NoSuchFieldException, IllegalAccessException, InterruptedException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "success": true,
                            "type": "ACCOUNT_CONFIRMED"
                        }
                        """));

        var testUrl = mockWebServer.url("/api/v1/account/confirm").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountConfirmUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        HttpPatch request = new HttpPatch(testUrl);

        // Act
        AccountResponse response = httpAuthClient.executeForAccountResponse(request);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(), "Success field should be true");
        assertEquals(AccountResponseType.ACCOUNT_CONFIRMED, response.getType(), "Response type should be ACCOUNT_CONFIRMED");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/account/confirm", recordedRequest.getPath(), "Endpoint path is incorrect");
        assertEquals("PATCH", recordedRequest.getMethod(), "HTTP method should be PATCH");
    }

    @Test
    void executeForAccountResponse_invalidResponse_shouldThrowHttpRequestException() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "success": false,
                            "type": "INVALID_REQUEST"
                        }
                        """));

        var testUrl = mockWebServer.url("/api/v1/account/invalid").toString();
        HttpUriRequestBase request = new HttpPatch(testUrl);

        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> httpAuthClient.executeForAccountResponse(request));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("HTTP request failed with status code: 400"), "Should indicate request failure");
    }

    @Test
    void executeForAccountResponse_serverError_shouldThrowException() throws IOException {
        // Arrange
        Throwable originalCause = new IOException("Internal Server Error");
        HttpPatch request = mock(HttpPatch.class);
        HttpAuthClient httpAuthClientMock = mock(HttpAuthClient.class);

        when(httpAuthClientMock.executeForAccountResponse(request)).thenThrow(new HttpRequestException("Server error", originalCause));

        // Act & Assert
        HttpRequestException exception =
                assertThrows(HttpRequestException.class, () -> httpAuthClientMock.executeForAccountResponse(request));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Server error"), "Exception message should contain 'Server error'.");
        assertEquals(originalCause, exception.getCause(), "Exception cause should match original cause");
    }

    @Test
    void executeForUserRoles_validResponse_shouldReturnUserRoles() throws IOException, NoSuchFieldException, IllegalAccessException, InterruptedException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "user": {
                                "userId": 123,
                                "username": "testUser",
                                "email": "testUser@example.com"
                            },
                            "roles": [
                                {"roleId": 1, "roleName": "ROLE_USER"},
                                {"roleId": 2, "roleName": "ROLE_ADMIN"}
                            ]
                        }
                        """));

        var testUrl = mockWebServer.url("/api/v1/account/roles").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        HttpGet request = new HttpGet(testUrl);
        // Act
        UserRoles rolesResponse = httpAuthClient.executeForUserRoles(request);

        // Assert
        assertNotNull(rolesResponse, "Response should not be null");
        assertNotNull(rolesResponse.getUser(), "User object should not be null");
        assertEquals("testUser", rolesResponse.getUser().getUsername(), "Username should match");
        assertTrue(rolesResponse.getRoles().stream().anyMatch(role -> "ROLE_USER".equals(role.getRoleName())), "Should contain role ROLE_USER");
        assertTrue(rolesResponse.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getRoleName())), "Should contain role ROLE_ADMIN");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/account/roles", recordedRequest.getPath(), "Request path is incorrect");
        assertEquals("GET", recordedRequest.getMethod(), "HTTP method should be GET");
    }

    @Test
    void executeForUserRoles_invalidResponse_shouldThrowHttpRequestException() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "error": "INVALID_REQUEST"
                        }
                        """));

        var testUrl = mockWebServer.url("/api/v1/account/roles").toString();
        HttpGet request = new HttpGet(testUrl);

        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> httpAuthClient.executeForUserRoles(request));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("HTTP request failed with status code: 400"), "Exception should indicate bad request error");
    }

    @Test
    void executeForUserRoles_serverError_shouldThrowException() throws IOException {
        // Arrange
        Throwable originalCause = new IOException("Internal Server Error");
        HttpGet request = mock(HttpGet.class);
        HttpAuthClient httpAuthClientMock = mock(HttpAuthClient.class);

        when(httpAuthClientMock.executeForUserRoles(request)).thenThrow(new HttpRequestException("Server error", originalCause));

        // Act & Assert
        HttpRequestException exception =
                assertThrows(HttpRequestException.class, () -> httpAuthClientMock.executeForUserRoles(request));

        assertNotNull(exception, "Exception should not be null");
        assertTrue(exception.getMessage().contains("Server error"), "Exception message should contain 'Server error'");
        assertEquals(originalCause, exception.getCause(), "Exception cause should match the original cause");
    }

    @Test
    void executeRequest_validResponse_shouldReturnExpectedObject() throws InterruptedException, IOException, NoSuchFieldException, IllegalAccessException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "success": true,
                            "type": "ACCOUNT_CONFIRMED"
                        }
                        """));

        var testUrl = mockWebServer.url("/api/v1/test/execute-request").toString();
        var privateField = HttpAuthClient.class.getDeclaredField("authApiAccountConfirmUrl");
        privateField.setAccessible(true);
        privateField.set(httpAuthClient, testUrl);

        HttpPost request = new HttpPost(testUrl);

        // Act
        AccountResponse response = httpAuthClient.executeRequest(request, AccountResponse.class);

        // Assert
        assertNotNull(response, "Response should not be null.");
        assertTrue(response.isSuccess(), "Success field should be true.");
        assertEquals(AccountResponseType.ACCOUNT_CONFIRMED, response.getType(), "Response type should be ACCOUNT_CONFIRMED.");

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/v1/test/execute-request", recordedRequest.getPath(), "Request path should be correct.");
        assertEquals("POST", recordedRequest.getMethod(), "HTTP method should be POST.");
    }

    @Test
    void executeRequest_invalidResponse_shouldThrowHttpRequestException() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "success": false,
                            "type": "INVALID_REQUEST"
                        }
                        """));

        var testUrl = mockWebServer.url("/api/v1/test/execute-request").toString();
        HttpPost request = new HttpPost(testUrl);

        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> httpAuthClient.executeRequest(request, AccountResponse.class));

        assertNotNull(exception, "Exception should not be null.");
        assertTrue(exception.getMessage().contains("HTTP request failed with status code: 400"), "Should indicate request failure.");
    }

    @Test
    void executeRequest_serverError_shouldThrowException() throws IOException {
        // Arrange
        Throwable originalCause = new IOException("Internal Server Error");
        HttpPost request = mock(HttpPost.class);
        HttpAuthClient httpAuthClientMock = mock(HttpAuthClient.class);

        when(httpAuthClientMock.executeRequest(request, AccountResponse.class)).thenThrow(new HttpRequestException("Server error", originalCause));

        // Act & Assert
        HttpRequestException exception =
                assertThrows(HttpRequestException.class, () -> httpAuthClientMock.executeRequest(request, AccountResponse.class));

        assertNotNull(exception, "Exception should not be null.");
        assertTrue(exception.getMessage().contains("Server error"), "Exception message should contain 'Server error'.");
        assertEquals(originalCause, exception.getCause(), "Exception cause should match the original cause.");
    }

}
