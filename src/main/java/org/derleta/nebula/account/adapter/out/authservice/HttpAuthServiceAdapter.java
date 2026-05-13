package org.derleta.nebula.account.adapter.out.authservice;

import lombok.RequiredArgsConstructor;
import org.derleta.nebula.account.adapter.out.authservice.dto.*;
import org.derleta.nebula.account.application.port.in.command.ChangePasswordCommand;
import org.derleta.nebula.account.application.port.out.AuthServicePort;
import org.derleta.nebula.account.domain.model.AccountOperationResult;
import org.derleta.nebula.account.domain.model.RegisteredAccountInfo;
import org.derleta.nebula.account.domain.model.TokenResult;
import org.derleta.nebula.shared.application.port.out.MetricsPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Outbound adapter — bridges AuthServicePort to the legacy HttpAuthClient.
 */
@Component
@RequiredArgsConstructor
public class HttpAuthServiceAdapter implements AuthServicePort {

    private static final String AUTH_SERVICE_REQUESTS_METRIC = "nebula.authservice.requests";
    private static final String AUTH_SERVICE_REQUEST_DURATION_METRIC = "nebula.authservice.request.duration";

    private final HttpAuthClient httpAuthClient;
    private final MetricsPort metricsPort;

    @Override
    public AccountOperationResult register(String username, String email, String password) {
        return recordOperation("register", () -> {
            String encrypted = new BCryptPasswordEncoder().encode(password);
            AuthServRegistrationRequest request = AuthServRegistrationRequest.builder()
                    .username(username).email(email).encryptedPassword(encrypted).build();
            return toResult(httpAuthClient.registerUser(request));
        }, AccountOperationResult::success);
    }

    @Override
    public AccountOperationResult confirm(long tokenId, String token) {
        return recordOperation("confirm",
                () -> toResult(httpAuthClient.confirmAccount(new AuthConfirmRequest(tokenId, token))),
                AccountOperationResult::success);
    }

    @Override
    public AccountOperationResult unlock(long accountId) {
        return recordOperation("unlock",
                () -> toResult(httpAuthClient.unlockAccount(accountId)),
                AccountOperationResult::success);
    }

    @Override
    public AccountOperationResult resetPassword(String email) {
        return recordOperation("reset-password",
                () -> toResult(httpAuthClient.resetPassword(email)),
                AccountOperationResult::success);
    }

    @Override
    public TokenResult generateToken(String email, String password) {
        return recordOperation("generate-token", () -> {
            AuthTokenResponse response = httpAuthClient.generateToken(new AuthTokenRequest(email, password));
            Map<String, String> cookies = response.getCookiesHeaders();
            // Full Set-Cookie strings forwarded directly to the REST controller
            return new TokenResult(
                    response.getUsername(), response.getEmail(),
                    cookies.get("accessToken"),
                    cookies.get("refreshToken")
            );
        }, result -> result.accessTokenCookie() != null && result.refreshTokenCookie() != null);
    }

    @Override
    public AccountOperationResult updatePassword(String jwtToken, ChangePasswordCommand cmd) {
        return recordOperation("change-password", () -> {
            AuthUpdatePasswordRequest request = new AuthUpdatePasswordRequest(
                    cmd.userId(), cmd.email(), cmd.actualPassword(), cmd.newPassword()
            );
            return toResult(httpAuthClient.updatePassword(jwtToken, request));
        }, AccountOperationResult::success);
    }

    @Override
    public RegisteredAccountInfo getAccount(String username, String email) {
        return recordOperation("get-account", () -> {
            var userRoles = httpAuthClient.getAccount(username, email);
            return new RegisteredAccountInfo(
                    userRoles.getUser().getUserId(),
                    userRoles.getUser().getUsername(),
                    userRoles.getUser().getEmail()
            );
        }, Objects::nonNull);
    }

    /**
     * Maps legacy AccountResponse to domain AccountOperationResult.
     */
    private static AccountOperationResult toResult(AccountResponse response) {
        return new AccountOperationResult(response.isSuccess(), response.getType());
    }

    private <T> T recordOperation(String operation, Supplier<T> action, Predicate<T> successEvaluator) {
        metricsPort.incrementCounter(AUTH_SERVICE_REQUESTS_METRIC,
                "operation", operation,
                "stage", "attempt");
        long start = System.nanoTime();
        try {
            T result = action.get();
            metricsPort.incrementCounter(AUTH_SERVICE_REQUESTS_METRIC,
                    "operation", operation,
                    "stage", successEvaluator.test(result) ? "success" : "failure");
            return result;
        } catch (RuntimeException exception) {
            metricsPort.incrementCounter(AUTH_SERVICE_REQUESTS_METRIC,
                    "operation", operation,
                    "stage", "error");
            throw exception;
        } finally {
            metricsPort.recordDuration(AUTH_SERVICE_REQUEST_DURATION_METRIC,
                    System.nanoTime() - start,
                    "operation", operation);
        }
    }
}
