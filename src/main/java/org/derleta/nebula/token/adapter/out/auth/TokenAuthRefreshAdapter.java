package org.derleta.nebula.token.adapter.out.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.derleta.nebula.account.adapter.in.rest.dto.response.AccessResponse;
import org.derleta.nebula.shared.adapter.in.rest.dto.Response;
import org.derleta.nebula.shared.application.port.out.MetricsPort;
import org.derleta.nebula.token.application.port.in.RefreshAccessTokenUseCase;
import org.derleta.nebula.account.adapter.out.authservice.HttpAuthClient;

import java.util.function.Supplier;

/**
 * Outbound auth adapter that delegates access-token refresh
 * to the external authentication service via {@link HttpAuthClient}.
 * Implements {@link RefreshAccessTokenUseCase}.
 */
@Component
@RequiredArgsConstructor
public class TokenAuthRefreshAdapter implements RefreshAccessTokenUseCase {

    private static final String REFRESH_ACCESS_OPERATION = "refresh-access";
    private static final String AUTH_SERVICE_REQUESTS_METRIC = "nebula.authservice.requests";
    private static final String AUTH_SERVICE_REQUEST_DURATION_METRIC = "nebula.authservice.request.duration";

    private final HttpAuthClient httpAuthClient;
    private final MetricsPort metricsPort;

    @Override
    public Response refreshAccess(final String refreshToken) {
        return recordRefreshAccess(() -> httpAuthClient.refreshAccess(refreshToken));
    }

    private Response recordRefreshAccess(Supplier<Response> action) {
        metricsPort.incrementCounter(AUTH_SERVICE_REQUESTS_METRIC,
                "operation", REFRESH_ACCESS_OPERATION,
                "stage", "attempt");
        long start = System.nanoTime();
        try {
            Response result = action.get();
            metricsPort.incrementCounter(AUTH_SERVICE_REQUESTS_METRIC,
                    "operation", REFRESH_ACCESS_OPERATION,
                    "stage", isSuccessfulRefresh(result) ? "success" : "failure");
            return result;
        } catch (RuntimeException exception) {
            metricsPort.incrementCounter(AUTH_SERVICE_REQUESTS_METRIC,
                    "operation", REFRESH_ACCESS_OPERATION,
                    "stage", "error");
            throw exception;
        } finally {
            metricsPort.recordDuration(AUTH_SERVICE_REQUEST_DURATION_METRIC,
                    System.nanoTime() - start,
                    "operation", REFRESH_ACCESS_OPERATION);
        }
    }

    private boolean isSuccessfulRefresh(Response response) {
        return response instanceof AccessResponse accessResponse && accessResponse.isSuccess();
    }
}

