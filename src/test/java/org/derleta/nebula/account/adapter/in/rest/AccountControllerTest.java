package org.derleta.nebula.account.adapter.in.rest;

import org.derleta.nebula.account.application.port.in.*;
import org.derleta.nebula.account.domain.model.AccountOperationResult;
import org.derleta.nebula.account.domain.model.TokenResult;
import org.derleta.nebula.account.domain.types.AccountResponseType;
import org.derleta.nebula.account.adapter.in.rest.dto.request.*;
import org.derleta.nebula.account.adapter.in.rest.dto.response.AccountOperationResponse;
import org.derleta.nebula.account.adapter.in.rest.dto.response.JwtTokenResponse;
import org.derleta.nebula.shared.domain.exception.HttpRequestException;
import org.derleta.nebula.shared.domain.exception.TokenExpiredException;
import org.derleta.nebula.shared.security.TokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock private RegisterAccountUseCase registerUC;
    @Mock private ConfirmAccountUseCase confirmUC;
    @Mock private UnlockAccountUseCase unlockUC;
    @Mock private ResetPasswordUseCase resetUC;
    @Mock private GenerateTokenUseCase tokenUC;
    @Mock private ChangePasswordUseCase changePassUC;
    @Mock private TokenProvider tokenProvider;
    @InjectMocks private AccountController controller;

    private static final AccountOperationResult SUCCESS =
            new AccountOperationResult(true, AccountResponseType.USER_CREATED_IN_NEBULA_DB);
    private static final AccountOperationResult FAILURE =
            new AccountOperationResult(false, AccountResponseType.NEBULA_BAD_REGISTRATION_RESPONSE_INSTANCE);

    @Test
    void register_success_returns200() {
        AccountRegistrationRequest req = AccountRegistrationRequest.builder()
                .login("user").email("u@t.com").password("pass1234")
                .birthdate(Date.valueOf(LocalDate.of(1990,1,1))).nationality(1).gender(2).build();
        when(registerUC.register(any())).thenReturn(SUCCESS);
        ResponseEntity<AccountOperationResponse> resp = controller.register(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody().isSuccess());
    }

    @Test
    void register_failure_returns409() {
        AccountRegistrationRequest req = AccountRegistrationRequest.builder()
                .login("u").email("u@t.com").password("pass1234")
                .nationality(1).gender(1).build();
        when(registerUC.register(any())).thenReturn(FAILURE);
        ResponseEntity<AccountOperationResponse> resp = controller.register(req);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void confirm_success_returns200() {
        when(confirmUC.confirm(anyLong(), anyString()))
                .thenReturn(new AccountOperationResult(true, AccountResponseType.ACCOUNT_CONFIRMED));
        ResponseEntity<AccountOperationResponse> resp = controller.confirm(new UserConfirmationRequest(1L, "tok"));
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void confirm_failure_returns409() {
        when(confirmUC.confirm(anyLong(), anyString()))
                .thenReturn(new AccountOperationResult(false, AccountResponseType.NEBULA_BAD_REGISTRATION_RESPONSE_INSTANCE));
        ResponseEntity<AccountOperationResponse> resp = controller.confirm(new UserConfirmationRequest(1L, "tok"));
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void confirm_tokenExpired_returns409WithExpiredType() {
        when(confirmUC.confirm(anyLong(), anyString())).thenThrow(new TokenExpiredException("expired"));
        ResponseEntity<AccountOperationResponse> resp = controller.confirm(new UserConfirmationRequest(1L, "tok"));
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertEquals(AccountResponseType.TOKEN_EXPIRED, resp.getBody().getType());
    }

    @Test
    void unlock_success_returns200() {
        when(unlockUC.unlock(anyLong()))
                .thenReturn(new AccountOperationResult(true, AccountResponseType.ACCOUNT_CAN_BE_UNLOCKED));
        assertEquals(HttpStatus.OK, controller.unlock(5L).getStatusCode());
    }

    @Test
    void unlock_failure_returns409() {
        when(unlockUC.unlock(anyLong()))
                .thenReturn(new AccountOperationResult(false, AccountResponseType.NEBULA_BAD_REGISTRATION_RESPONSE_INSTANCE));
        assertEquals(HttpStatus.CONFLICT, controller.unlock(5L).getStatusCode());
    }

    @Test
    void unlock_httpException_returns401() {
        when(unlockUC.unlock(anyLong())).thenThrow(new HttpRequestException("err", new RuntimeException()));
        assertEquals(HttpStatus.UNAUTHORIZED, controller.unlock(5L).getStatusCode());
    }

    @Test
    void resetPassword_success_returns200() {
        when(resetUC.resetPassword("e@t.com"))
                .thenReturn(new AccountOperationResult(true, AccountResponseType.PASSWORD_CAN_BE_CHANGED));
        assertEquals(HttpStatus.OK, controller.resetPassword("e@t.com").getStatusCode());
    }

    @Test
    void resetPassword_failure_returns409() {
        when(resetUC.resetPassword("e@t.com"))
                .thenReturn(new AccountOperationResult(false, AccountResponseType.NEBULA_BAD_REGISTRATION_RESPONSE_INSTANCE));
        assertEquals(HttpStatus.CONFLICT, controller.resetPassword("e@t.com").getStatusCode());
    }

    @Test
    void resetPassword_tokenExpired_returns409() {
        when(resetUC.resetPassword(anyString())).thenThrow(new TokenExpiredException("exp"));
        ResponseEntity<AccountOperationResponse> r = controller.resetPassword("e@t.com");
        assertEquals(HttpStatus.CONFLICT, r.getStatusCode());
        assertEquals(AccountResponseType.PASSWORD_RESET_ACCESS_TOKEN_EXPIRED, r.getBody().getType());
    }

    @Test
    void getToken_returns200WithCookieHeaders() {
        AuthEmailRequest req = new AuthEmailRequest("e@t.com", "pass");
        when(tokenUC.generateToken("e@t.com", "pass"))
                .thenReturn(new TokenResult("user", "e@t.com", "acc-cookie", "ref-cookie"));
        ResponseEntity<JwtTokenResponse> resp = controller.getToken(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("user", resp.getBody().getUsername());
        assertTrue(resp.getHeaders().containsKey("Set-Cookie"));
    }

    @Test
    void changePassword_invalidToken_returns403() {
        when(tokenProvider.isValid(anyString(), anyLong())).thenReturn(false);
        ResponseEntity<AccountOperationResponse> resp = controller.changePassword(
                "bad-jwt", new PasswordUpdateRequest(1L, "e@t.com", "old", "new"));
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    @Test
    void changePassword_validToken_success_returns200() {
        when(tokenProvider.isValid(anyString(), anyLong())).thenReturn(true);
        when(changePassUC.changePassword(anyString(), any()))
                .thenReturn(new AccountOperationResult(true, AccountResponseType.PASSWORD_CHANGED));
        ResponseEntity<AccountOperationResponse> resp = controller.changePassword(
                "jwt", new PasswordUpdateRequest(1L, "e@t.com", "old", "new"));
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void changePassword_validToken_failure_returns409() {
        when(tokenProvider.isValid(anyString(), anyLong())).thenReturn(true);
        when(changePassUC.changePassword(anyString(), any()))
                .thenReturn(new AccountOperationResult(false, AccountResponseType.NEBULA_BAD_REGISTRATION_RESPONSE_INSTANCE));
        assertEquals(HttpStatus.CONFLICT, controller.changePassword(
                "jwt", new PasswordUpdateRequest(1L, "e@t.com", "old", "new")).getStatusCode());
    }

    @Test
    void getToken_sanitizesCookieValues() {
        AuthEmailRequest request = new AuthEmailRequest("test@example.com", "password");
        // Malicious cookie values with CRLF
        String maliciousAccess = "accessToken=val\r\nInjected-Header: evil";
        String maliciousRefresh = "refreshToken=val\nAnother-Header: evil";

        TokenResult result = new TokenResult("user", "test@example.com", maliciousAccess, maliciousRefresh);

        when(tokenUC.generateToken(request.getEmail(), request.getPassword())).thenReturn(result);

        ResponseEntity<JwtTokenResponse> response = controller.getToken(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<String> setCookies = response.getHeaders().get("Set-Cookie");
        assertNotNull(setCookies);
        assertEquals(2, setCookies.size());

        for (String cookie : setCookies) {
            assertFalse(cookie.contains("\r"), "Cookie should not contain \\r");
            assertFalse(cookie.contains("\n"), "Cookie should not contain \\n");
            assertFalse(cookie.contains(":"), "Cookie should not contain :");
        }
        assertTrue(setCookies.get(0).contains("accessToken=valInjected-Header evil"));
        assertTrue(setCookies.get(1).contains("refreshToken=valAnother-Header evil"));
    }
}
