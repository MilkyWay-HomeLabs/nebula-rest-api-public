package org.derleta.nebula.token.adapter.out.auth;

import org.derleta.nebula.account.adapter.in.rest.dto.response.AccessResponse;
import org.derleta.nebula.shared.adapter.in.rest.dto.Response;
import org.derleta.nebula.account.adapter.out.authservice.HttpAuthClient;
import org.derleta.nebula.shared.application.port.out.MetricsPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenAuthRefreshAdapterTest {

    @Mock
    private HttpAuthClient httpAuthClient;

    @Mock
    private MetricsPort metricsPort;

    @InjectMocks
    private TokenAuthRefreshAdapter adapter;

    @Test
    void refreshAccess_validToken_returnsSuccessResponse() {
        String refreshToken = "valid-refresh-token";
        AccessResponse expected = new AccessResponse(Map.of(), true, "ACCESS_REFRESHED");
        when(httpAuthClient.refreshAccess(refreshToken)).thenReturn(expected);

        Response result = adapter.refreshAccess(refreshToken);

        assertEquals(expected, result);
        verify(metricsPort).incrementCounter("nebula.authservice.requests", "operation", "refresh-access", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.authservice.requests", "operation", "refresh-access", "stage", "success");
        verify(metricsPort).recordDuration(eq("nebula.authservice.request.duration"), anyLong(), eq("operation"), eq("refresh-access"));
    }

    @Test
    void refreshAccess_whenClientReturnsNull_returnsNull() {
        String refreshToken = "valid-refresh-token";
        when(httpAuthClient.refreshAccess(refreshToken)).thenReturn(null);

        Response result = adapter.refreshAccess(refreshToken);

        assertNull(result);
        verify(metricsPort).incrementCounter("nebula.authservice.requests", "operation", "refresh-access", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.authservice.requests", "operation", "refresh-access", "stage", "failure");
        verify(metricsPort).recordDuration(eq("nebula.authservice.request.duration"), anyLong(), eq("operation"), eq("refresh-access"));
    }

    @Test
    void refreshAccess_whenClientThrows_recordsErrorMetrics() {
        String refreshToken = "valid-refresh-token";
        when(httpAuthClient.refreshAccess(refreshToken)).thenThrow(new IllegalStateException("downstream failure"));

        assertThrows(IllegalStateException.class, () -> adapter.refreshAccess(refreshToken));

        verify(metricsPort).incrementCounter("nebula.authservice.requests", "operation", "refresh-access", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.authservice.requests", "operation", "refresh-access", "stage", "error");
        verify(metricsPort).recordDuration(eq("nebula.authservice.request.duration"), anyLong(), eq("operation"), eq("refresh-access"));
    }
}

