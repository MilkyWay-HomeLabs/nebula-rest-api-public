package org.derleta.nebula.account.adapter.in.rest.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.account.adapter.in.rest.dto.request.AccountRegistrationRequest;
import org.derleta.nebula.account.adapter.in.rest.dto.request.PasswordUpdateRequest;
import org.derleta.nebula.account.adapter.in.rest.dto.response.AccountOperationResponse;
import org.derleta.nebula.account.adapter.in.rest.dto.response.JwtTokenResponse;
import org.derleta.nebula.account.application.port.in.command.ChangePasswordCommand;
import org.derleta.nebula.account.application.port.in.command.RegisterAccountCommand;
import org.derleta.nebula.account.domain.model.AccountOperationResult;
import org.derleta.nebula.account.domain.model.TokenResult;
import org.derleta.nebula.account.domain.types.AccountResponseType;

/** Maps REST request DTOs to application commands and domain results to REST response DTOs. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountRestMapper {

    // --- Request → Command ---

    /** Converts registration request DTO to a domain command. */
    public static RegisterAccountCommand toCommand(AccountRegistrationRequest request) {
        return new RegisterAccountCommand(
                request.getLogin(), request.getEmail(), request.getPassword(),
                request.getBirthdate(), request.getNationality(), request.getGender()
        );
    }

    /** Converts password update request DTO to a domain command. */
    public static ChangePasswordCommand toCommand(PasswordUpdateRequest request) {
        return new ChangePasswordCommand(
                request.getUserId(), request.getEmail(),
                request.getActualPassword(), request.getNewPassword()
        );
    }

    // --- Domain Result → Response DTO ---

    /** Converts a domain operation result to a REST response. */
    public static AccountOperationResponse toResponse(AccountOperationResult result) {
        return new AccountOperationResponse(result.success(), result.type());
    }

    /** Builds a safe conflict response with the given type. */
    public static AccountOperationResponse toConflictResponse(AccountResponseType type) {
        return new AccountOperationResponse(false, type);
    }

    /** Converts a domain token result to a REST response (without cookies — controller sets headers). */
    public static JwtTokenResponse toJwtResponse(TokenResult result) {
        return new JwtTokenResponse(null, result.username(), result.email());
    }
}
