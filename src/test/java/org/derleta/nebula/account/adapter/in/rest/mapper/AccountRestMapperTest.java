package org.derleta.nebula.account.adapter.in.rest.mapper;

import org.derleta.nebula.account.adapter.in.rest.dto.request.AccountRegistrationRequest;
import org.derleta.nebula.account.adapter.in.rest.dto.request.PasswordUpdateRequest;
import org.derleta.nebula.account.adapter.in.rest.dto.response.AccountOperationResponse;
import org.derleta.nebula.account.adapter.in.rest.dto.response.JwtTokenResponse;
import org.derleta.nebula.account.application.port.in.command.ChangePasswordCommand;
import org.derleta.nebula.account.application.port.in.command.RegisterAccountCommand;
import org.derleta.nebula.account.domain.model.AccountOperationResult;
import org.derleta.nebula.account.domain.model.TokenResult;
import org.derleta.nebula.account.domain.types.AccountResponseType;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AccountRestMapperTest {

    @Test
    void toCommand_fromRegistrationRequest_mapsAllFields() {
        AccountRegistrationRequest request = AccountRegistrationRequest.builder()
                .login("testuser")
                .email("test@example.com")
                .password("password123")
                .birthdate(Date.valueOf(LocalDate.of(1990, 1, 1)))
                .nationality(1)
                .gender(2)
                .build();

        RegisterAccountCommand command = AccountRestMapper.toCommand(request);

        assertNotNull(command);
        assertEquals("testuser", command.login());
        assertEquals("test@example.com", command.email());
        assertEquals("password123", command.password());
        assertEquals(1, command.nationalityId());
        assertEquals(2, command.genderId());
    }

    @Test
    void toCommand_fromPasswordUpdateRequest_mapsAllFields() {
        PasswordUpdateRequest request = new PasswordUpdateRequest(
                123L, "user@example.com", "oldPass", "newPass");

        ChangePasswordCommand command = AccountRestMapper.toCommand(request);

        assertNotNull(command);
        assertEquals(123L, command.userId());
        assertEquals("user@example.com", command.email());
        assertEquals("oldPass", command.actualPassword());
        assertEquals("newPass", command.newPassword());
    }

    @Test
    void toResponse_success_returnsSuccessResponse() {
        AccountOperationResult result = new AccountOperationResult(true, AccountResponseType.USER_CREATED_IN_NEBULA_DB);

        AccountOperationResponse response = AccountRestMapper.toResponse(result);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(AccountResponseType.USER_CREATED_IN_NEBULA_DB, response.getType());
    }

    @Test
    void toResponse_failure_returnsFailureResponse() {
        AccountOperationResult result = new AccountOperationResult(false, AccountResponseType.NEBULA_BAD_REGISTRATION_RESPONSE_INSTANCE);

        AccountOperationResponse response = AccountRestMapper.toResponse(result);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.NEBULA_BAD_REGISTRATION_RESPONSE_INSTANCE, response.getType());
    }

    @Test
    void toConflictResponse_buildsFailureWithGivenType() {
        AccountOperationResponse response = AccountRestMapper.toConflictResponse(AccountResponseType.TOKEN_EXPIRED);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(AccountResponseType.TOKEN_EXPIRED, response.getType());
    }

    @Test
    void toJwtResponse_mapsUsernameAndEmail() {
        TokenResult token = new TokenResult("myUser", "my@email.com", "access-cookie", "refresh-cookie");

        JwtTokenResponse response = AccountRestMapper.toJwtResponse(token);

        assertNotNull(response);
        assertEquals("myUser", response.getUsername());
        assertEquals("my@email.com", response.getEmail());
        assertNull(response.getCookiesHeaders()); // controller sets headers
    }
}

