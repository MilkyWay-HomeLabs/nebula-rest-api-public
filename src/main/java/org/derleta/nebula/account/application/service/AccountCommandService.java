package org.derleta.nebula.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.derleta.nebula.account.application.port.in.*;
import org.derleta.nebula.account.application.port.in.command.ChangePasswordCommand;
import org.derleta.nebula.account.application.port.in.command.RegisterAccountCommand;
import org.derleta.nebula.account.application.port.out.AccountRepositoryPort;
import org.derleta.nebula.account.application.port.out.AuthServicePort;
import org.derleta.nebula.account.domain.model.*;
import org.derleta.nebula.account.domain.types.AccountResponseType;
import org.derleta.nebula.shared.application.port.out.MetricsPort;

import java.util.function.Predicate;
import java.util.function.Supplier;

/** Application service orchestrating all account write operations. */
@Service
@RequiredArgsConstructor
public class AccountCommandService implements
        RegisterAccountUseCase, ConfirmAccountUseCase,
        UnlockAccountUseCase, ResetPasswordUseCase,
        GenerateTokenUseCase, ChangePasswordUseCase {

    private static final String ACCOUNT_OPERATIONS_METRIC = "nebula.account.operations";
    private static final String ACCOUNT_OPERATION_DURATION_METRIC = "nebula.account.operation.duration";

    private final AuthServicePort authServicePort;
    private final AccountRepositoryPort accountRepositoryPort;
    private final MetricsPort metricsPort;

    @Override
    @Transactional
    public AccountOperationResult register(RegisterAccountCommand command) {
        return recordOperation("register", () -> {
            // Step 1: register credentials on the external auth service
            AccountOperationResult authResult = authServicePort.register(
                    command.login(), command.email(), command.password()
            );
            if (!authResult.success()) {
                return new AccountOperationResult(false,
                        AccountResponseType.NEBULA_BAD_REGISTRATION_RESPONSE_INSTANCE);
            }

            // Step 2: fetch the assigned userId from the auth service
            RegisteredAccountInfo info = authServicePort.getAccount(
                    command.login(), command.email()
            );

            // Step 3: persist the account in the Nebula database
            Account account = new Account(
                    info.userId(), info.email(), info.username(),
                    command.birthDate(), command.nationalityId(), command.genderId()
            );
            Account saved = accountRepositoryPort.save(account);

            if (saved.id() == info.userId()) {
                return new AccountOperationResult(true,
                        AccountResponseType.USER_CREATED_IN_NEBULA_DB);
            }
            return new AccountOperationResult(false,
                    AccountResponseType.USER_NOT_CREATED_IN_NEBULA_DB);
        }, AccountOperationResult::success);
    }

    @Override
    public AccountOperationResult confirm(long tokenId, String token) {
        return recordOperation("confirm",
                () -> authServicePort.confirm(tokenId, token),
                AccountOperationResult::success);
    }

    @Override
    public AccountOperationResult unlock(long accountId) {
        return recordOperation("unlock",
                () -> authServicePort.unlock(accountId),
                AccountOperationResult::success);
    }

    @Override
    public AccountOperationResult resetPassword(String email) {
        return recordOperation("reset-password",
                () -> authServicePort.resetPassword(email),
                AccountOperationResult::success);
    }

    @Override
    public TokenResult generateToken(String email, String password) {
        return recordOperation("generate-token",
                () -> authServicePort.generateToken(email, password),
                result -> result.accessTokenCookie() != null && result.refreshTokenCookie() != null);
    }

    @Override
    public AccountOperationResult changePassword(String jwtToken, ChangePasswordCommand command) {
        return recordOperation("change-password",
                () -> authServicePort.updatePassword(jwtToken, command),
                AccountOperationResult::success);
    }

    private <T> T recordOperation(String operation, Supplier<T> action, Predicate<T> successEvaluator) {
        metricsPort.incrementCounter(ACCOUNT_OPERATIONS_METRIC,
                "operation", operation,
                "stage", "attempt");
        long start = System.nanoTime();
        try {
            T result = action.get();
            metricsPort.incrementCounter(ACCOUNT_OPERATIONS_METRIC,
                    "operation", operation,
                    "stage", successEvaluator.test(result) ? "success" : "failure");
            return result;
        } catch (RuntimeException exception) {
            metricsPort.incrementCounter(ACCOUNT_OPERATIONS_METRIC,
                    "operation", operation,
                    "stage", "error");
            throw exception;
        } finally {
            metricsPort.recordDuration(ACCOUNT_OPERATION_DURATION_METRIC,
                    System.nanoTime() - start,
                    "operation", operation);
        }
    }
}
