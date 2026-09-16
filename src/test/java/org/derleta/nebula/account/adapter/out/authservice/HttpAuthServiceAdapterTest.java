package org.derleta.nebula.account.adapter.out.authservice;

import org.derleta.nebula.account.adapter.out.authservice.dto.AccountResponse;
import org.derleta.nebula.account.adapter.out.authservice.dto.AuthTokenResponse;
import org.derleta.nebula.account.adapter.out.authservice.dto.UserAccount;
import org.derleta.nebula.account.adapter.out.authservice.dto.UserRoles;
import org.derleta.nebula.account.application.port.in.command.ChangePasswordCommand;
import org.derleta.nebula.account.domain.model.AccountOperationResult;
import org.derleta.nebula.account.domain.model.RegisteredAccountInfo;
import org.derleta.nebula.account.domain.model.TokenResult;
import org.derleta.nebula.account.domain.types.AccountResponseType;
import org.derleta.nebula.shared.application.port.out.MetricsPort;
import org.derleta.nebula.shared.security.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpAuthServiceAdapterTest {

    @Mock
    private HttpAuthClient httpAuthClient;

    @Mock
    private MetricsPort metricsPort;

    @InjectMocks
    private HttpAuthServiceAdapter adapter;

    @Test
    void generateToken_success_mapsCookiesAndRecordsMetrics() {
        AuthTokenResponse response = new AuthTokenResponse(
                Map.of(
                        "accessToken", "accessToken=abc; Path=/; HttpOnly",
                        "refreshToken", "refreshToken=def; Path=/; HttpOnly"
                ),
                "wolf",
                "wolf@milkyway.test"
        );
        when(httpAuthClient.generateToken(any())).thenReturn(response);

        TokenResult result = adapter.generateToken("wolf@milkyway.test", "secret");

        assertEquals("wolf", result.username());
        assertEquals("wolf@milkyway.test", result.email());
        assertEquals("accessToken=abc; Path=/; HttpOnly", result.accessTokenCookie());
        assertEquals("refreshToken=def; Path=/; HttpOnly", result.refreshTokenCookie());
        verify(metricsPort).incrementCounter("nebula.authservice.requests", "operation", "generate-token", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.authservice.requests", "operation", "generate-token", "stage", "success");
        verify(metricsPort).recordDuration(eq("nebula.authservice.request.duration"), anyLong(), eq("operation"), eq("generate-token"));
    }

    @Test
    void confirm_failure_recordsFailureMetrics() {
        when(httpAuthClient.confirmAccount(any())).thenReturn(new AccountResponse(false, AccountResponseType.TOKEN_EXPIRED));

        AccountOperationResult result = adapter.confirm(99L, "expired-token");

        assertEquals(AccountResponseType.TOKEN_EXPIRED, result.type());
        assertFalse(result.success());
        verify(metricsPort).incrementCounter("nebula.authservice.requests", "operation", "confirm", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.authservice.requests", "operation", "confirm", "stage", "failure");
        verify(metricsPort).recordDuration(eq("nebula.authservice.request.duration"), anyLong(), eq("operation"), eq("confirm"));
    }

    @Test
    void getAccount_success_mapsRegisteredAccountInfoAndRecordsMetrics() {
        UserRoles userRoles = new UserRoles(
                new UserAccount(321L, "wolf", "wolf@milkyway.test"),
                Set.of(new Role(1, "ROLE_USER"))
        );
        when(httpAuthClient.getAccount("wolf", "wolf@milkyway.test")).thenReturn(userRoles);

        RegisteredAccountInfo result = adapter.getAccount("wolf", "wolf@milkyway.test");

        assertEquals(321L, result.userId());
        assertEquals("wolf", result.username());
        assertEquals("wolf@milkyway.test", result.email());
        verify(metricsPort).incrementCounter("nebula.authservice.requests", "operation", "get-account", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.authservice.requests", "operation", "get-account", "stage", "success");
        verify(metricsPort).recordDuration(eq("nebula.authservice.request.duration"), anyLong(), eq("operation"), eq("get-account"));
    }

    @Test
    void changePassword_error_recordsErrorMetrics() {
        ChangePasswordCommand command = new ChangePasswordCommand(7L, "wolf@milkyway.test", "old-password", "new-password");
        when(httpAuthClient.updatePassword(eq("jwt-token"), any())).thenThrow(new IllegalStateException("downstream failure"));

        assertThrows(IllegalStateException.class, () -> adapter.updatePassword("jwt-token", command));

        verify(metricsPort).incrementCounter("nebula.authservice.requests", "operation", "change-password", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.authservice.requests", "operation", "change-password", "stage", "error");
        verify(metricsPort).recordDuration(eq("nebula.authservice.request.duration"), anyLong(), eq("operation"), eq("change-password"));
    }
}

