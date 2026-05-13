package org.derleta.nebula.account.application.service;
import org.derleta.nebula.account.application.port.in.command.ChangePasswordCommand;
import org.derleta.nebula.account.application.port.in.command.RegisterAccountCommand;
import org.derleta.nebula.account.application.port.out.AccountRepositoryPort;
import org.derleta.nebula.account.application.port.out.AuthServicePort;
import org.derleta.nebula.account.domain.model.*;
import org.derleta.nebula.account.domain.types.AccountResponseType;
import org.derleta.nebula.shared.application.port.out.MetricsPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AccountCommandServiceTest {
    @Mock private AuthServicePort authServicePort;
    @Mock private AccountRepositoryPort accountRepositoryPort;
    @Mock private MetricsPort metricsPort;
    @InjectMocks private AccountCommandService service;
    @Test
    void register_authSucceeds_savesAndReturnsCreated() {
        RegisterAccountCommand cmd = new RegisterAccountCommand("u","e@t.com","p",new Date(),1,2);
        when(authServicePort.register("u","e@t.com","p"))
            .thenReturn(new AccountOperationResult(true, AccountResponseType.VERIFICATION_MAIL_FROM_REGISTRATION));
        when(authServicePort.getAccount("u","e@t.com"))
            .thenReturn(new RegisteredAccountInfo(100L,"e@t.com","u"));
        when(accountRepositoryPort.save(any()))
            .thenReturn(new Account(100L,"e@t.com","u",new Date(),1,2));
        AccountOperationResult r = service.register(cmd);
        assertTrue(r.success());
        assertEquals(AccountResponseType.USER_CREATED_IN_NEBULA_DB, r.type());
        verify(metricsPort).incrementCounter("nebula.account.operations", "operation", "register", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.account.operations", "operation", "register", "stage", "success");
        verify(metricsPort).recordDuration(eq("nebula.account.operation.duration"), anyLong(), eq("operation"), eq("register"));
    }
    @Test
    void register_authFails_returnsFailure() {
        RegisterAccountCommand cmd = new RegisterAccountCommand("u","e@t.com","p",new Date(),1,2);
        when(authServicePort.register(any(),any(),any()))
            .thenReturn(new AccountOperationResult(false, AccountResponseType.NEBULA_BAD_REGISTRATION_RESPONSE_INSTANCE));
        assertFalse(service.register(cmd).success());
        verifyNoInteractions(accountRepositoryPort);
        verify(metricsPort).incrementCounter("nebula.account.operations", "operation", "register", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.account.operations", "operation", "register", "stage", "failure");
        verify(metricsPort).recordDuration(eq("nebula.account.operation.duration"), anyLong(), eq("operation"), eq("register"));
    }
    @Test
    void register_idMismatch_returnsNotCreated() {
        RegisterAccountCommand cmd = new RegisterAccountCommand("u","e@t.com","p",new Date(),1,2);
        when(authServicePort.register(any(),any(),any()))
            .thenReturn(new AccountOperationResult(true, AccountResponseType.VERIFICATION_MAIL_FROM_REGISTRATION));
        when(authServicePort.getAccount(any(),any()))
            .thenReturn(new RegisteredAccountInfo(100L,"e@t.com","u"));
        when(accountRepositoryPort.save(any()))
            .thenReturn(new Account(999L,"e@t.com","u",new Date(),1,2));
        assertEquals(AccountResponseType.USER_NOT_CREATED_IN_NEBULA_DB, service.register(cmd).type());
    }
    @Test
    void register_whenAuthServiceThrows_recordsErrorMetrics() {
        RegisterAccountCommand cmd = new RegisterAccountCommand("u","e@t.com","p",new Date(),1,2);
        when(authServicePort.register("u","e@t.com","p"))
            .thenThrow(new IllegalStateException("auth service unavailable"));

        assertThrows(IllegalStateException.class, () -> service.register(cmd));

        verifyNoInteractions(accountRepositoryPort);
        verify(metricsPort).incrementCounter("nebula.account.operations", "operation", "register", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.account.operations", "operation", "register", "stage", "error");
        verify(metricsPort).recordDuration(eq("nebula.account.operation.duration"), anyLong(), eq("operation"), eq("register"));
    }
    @Test void confirm_delegates() {
        when(authServicePort.confirm(42L,"tok")).thenReturn(new AccountOperationResult(true, AccountResponseType.ACCOUNT_CONFIRMED));
        assertTrue(service.confirm(42L,"tok").success());
    }
    @Test void unlock_delegates() {
        when(authServicePort.unlock(10L)).thenReturn(new AccountOperationResult(true, AccountResponseType.ACCOUNT_CAN_BE_UNLOCKED));
        assertTrue(service.unlock(10L).success());
    }
    @Test void resetPassword_delegates() {
        when(authServicePort.resetPassword("e@t.com")).thenReturn(new AccountOperationResult(true, AccountResponseType.PASSWORD_CAN_BE_CHANGED));
        assertTrue(service.resetPassword("e@t.com").success());
    }
    @Test void generateToken_delegates() {
        TokenResult expected = new TokenResult("u","e@t.com","acc","ref");
        when(authServicePort.generateToken("e@t.com","pass")).thenReturn(expected);
        assertEquals(expected, service.generateToken("e@t.com","pass"));
        verify(metricsPort).incrementCounter("nebula.account.operations", "operation", "generate-token", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.account.operations", "operation", "generate-token", "stage", "success");
        verify(metricsPort).recordDuration(eq("nebula.account.operation.duration"), anyLong(), eq("operation"), eq("generate-token"));
    }
    @Test void generateToken_missingCookies_recordsFailureMetrics() {
        TokenResult failed = new TokenResult("u","e@t.com",null,"ref");
        when(authServicePort.generateToken("e@t.com","pass")).thenReturn(failed);

        assertEquals(failed, service.generateToken("e@t.com","pass"));

        verify(metricsPort).incrementCounter("nebula.account.operations", "operation", "generate-token", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.account.operations", "operation", "generate-token", "stage", "failure");
        verify(metricsPort).recordDuration(eq("nebula.account.operation.duration"), anyLong(), eq("operation"), eq("generate-token"));
    }
    @Test void changePassword_delegates() {
        ChangePasswordCommand cmd = new ChangePasswordCommand(1L,"u@t.com","old","new");
        when(authServicePort.updatePassword("jwt",cmd)).thenReturn(new AccountOperationResult(true, AccountResponseType.PASSWORD_CHANGED));
        assertTrue(service.changePassword("jwt",cmd).success());
        verify(metricsPort).incrementCounter("nebula.account.operations", "operation", "change-password", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.account.operations", "operation", "change-password", "stage", "success");
        verify(metricsPort).recordDuration(eq("nebula.account.operation.duration"), anyLong(), eq("operation"), eq("change-password"));
    }
    @Test void changePassword_whenAuthServiceThrows_recordsErrorMetrics() {
        ChangePasswordCommand cmd = new ChangePasswordCommand(1L,"u@t.com","old","new");
        when(authServicePort.updatePassword("jwt",cmd)).thenThrow(new IllegalStateException("change-password failed"));

        assertThrows(IllegalStateException.class, () -> service.changePassword("jwt",cmd));

        verify(metricsPort).incrementCounter("nebula.account.operations", "operation", "change-password", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.account.operations", "operation", "change-password", "stage", "error");
        verify(metricsPort).recordDuration(eq("nebula.account.operation.duration"), anyLong(), eq("operation"), eq("change-password"));
    }
}
