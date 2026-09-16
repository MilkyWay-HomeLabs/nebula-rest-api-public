package org.derleta.nebula.account.adapter.in.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.derleta.nebula.account.adapter.in.rest.dto.request.AccountRegistrationRequest;
import org.derleta.nebula.account.adapter.in.rest.dto.request.AuthEmailRequest;
import org.derleta.nebula.account.adapter.in.rest.dto.request.PasswordUpdateRequest;
import org.derleta.nebula.account.adapter.in.rest.dto.request.UserConfirmationRequest;
import org.derleta.nebula.account.adapter.in.rest.dto.response.AccountOperationResponse;
import org.derleta.nebula.account.adapter.in.rest.dto.response.JwtTokenResponse;
import org.derleta.nebula.account.adapter.in.rest.mapper.AccountRestMapper;
import org.derleta.nebula.account.application.port.in.*;
import org.derleta.nebula.account.domain.model.TokenResult;
import org.derleta.nebula.account.domain.types.AccountResponseType;
import org.derleta.nebula.shared.domain.exception.HttpRequestException;
import org.derleta.nebula.shared.domain.exception.TokenExpiredException;
import org.derleta.nebula.shared.security.TokenProvider;
import org.derleta.nebula.shared.util.CookieSanitizer;

/** REST adapter — exposes account management endpoints. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AccountController {

    public static final String DEFAULT_PATH = "account";

    private final RegisterAccountUseCase registerAccountUseCase;
    private final ConfirmAccountUseCase confirmAccountUseCase;
    private final UnlockAccountUseCase unlockAccountUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final GenerateTokenUseCase generateTokenUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final TokenProvider tokenProvider;

    @PostMapping(value = "/" + DEFAULT_PATH + "/register", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<AccountOperationResponse> register(
            @Valid @RequestBody AccountRegistrationRequest request) {
        var result = registerAccountUseCase.register(AccountRestMapper.toCommand(request));
        if (result.success()) {
            return ResponseEntity.ok(AccountRestMapper.toResponse(result));
        }
        return buildConflictResponse(result.type());
    }

    @PatchMapping(value = "/" + DEFAULT_PATH + "/confirm", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<AccountOperationResponse> confirm(
            @RequestBody UserConfirmationRequest request) {
        try {
            var result = confirmAccountUseCase.confirm(request.tokenId(), request.token());
            if (result.success()) {
                return ResponseEntity.ok(AccountRestMapper.toResponse(result));
            }
            return buildConflictResponse(result.type());
        } catch (TokenExpiredException e) {
            return buildConflictResponse(AccountResponseType.TOKEN_EXPIRED);
        }
    }

    @PatchMapping(value = "/" + DEFAULT_PATH + "/unlock/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<AccountOperationResponse> unlock(@PathVariable Long id) {
        try {
            var result = unlockAccountUseCase.unlock(id);
            if (result.success()) {
                return ResponseEntity.ok(AccountRestMapper.toResponse(result));
            }
            return buildConflictResponse(result.type());
        } catch (HttpRequestException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AccountRestMapper.toConflictResponse(AccountResponseType.BAD_UNLOCK_HTTP_REQUEST));
        }
    }

    @PatchMapping(value = "/" + DEFAULT_PATH + "/reset-password/{email}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<AccountOperationResponse> resetPassword(@PathVariable String email) {
        try {
            var result = resetPasswordUseCase.resetPassword(email);
            if (result.success()) {
                return ResponseEntity.ok(AccountRestMapper.toResponse(result));
            }
            return buildConflictResponse(result.type());
        } catch (TokenExpiredException e) {
            return buildConflictResponse(AccountResponseType.PASSWORD_RESET_ACCESS_TOKEN_EXPIRED);
        }
    }

    @PostMapping(value = "/" + DEFAULT_PATH + "/token", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<JwtTokenResponse> getToken(@RequestBody AuthEmailRequest request) {
        TokenResult result = generateTokenUseCase.generateToken(request.getEmail(), request.getPassword());
        var responseBuilder = ResponseEntity.ok();

        if (result.accessTokenCookie() != null) {
            responseBuilder.header("Set-Cookie", CookieSanitizer.sanitize(result.accessTokenCookie()));
        }
        if (result.refreshTokenCookie() != null) {
            responseBuilder.header("Set-Cookie", CookieSanitizer.sanitize(result.refreshTokenCookie()));
        }

        return responseBuilder.body(AccountRestMapper.toJwtResponse(result));
    }

    @PostMapping(value = "/" + DEFAULT_PATH + "/change-password", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<AccountOperationResponse> changePassword(
            @CookieValue("accessToken") String accessToken,
            @RequestBody PasswordUpdateRequest request) {
        if (!tokenProvider.isValid(accessToken, request.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var result = changePasswordUseCase.changePassword(accessToken, AccountRestMapper.toCommand(request));
        if (result.success()) {
            return ResponseEntity.ok(AccountRestMapper.toResponse(result));
        }
        return buildConflictResponse(result.type());
    }

    private ResponseEntity<AccountOperationResponse> buildConflictResponse(AccountResponseType type) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(AccountRestMapper.toConflictResponse(type));
    }
}
