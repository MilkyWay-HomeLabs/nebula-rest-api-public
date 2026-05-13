package org.derleta.nebula.shared.adapter.in.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.derleta.nebula.account.domain.exception.AccountNotFoundException;
import org.derleta.nebula.shared.adapter.in.rest.dto.ErrorResponse;
import org.derleta.nebula.shared.domain.exception.HttpRequestException;
import org.derleta.nebula.shared.domain.exception.MissingHeaderException;
import org.derleta.nebula.shared.domain.exception.TokenExpiredException;
import org.derleta.nebula.game.domain.exception.GameAlreadyExistsException;
import org.derleta.nebula.game.domain.exception.GameNotFoundException;
import org.derleta.nebula.gender.domain.exception.GenderNotFoundException;
import org.derleta.nebula.nationality.domain.exception.NationalityNotFoundException;
import org.derleta.nebula.theme.domain.exception.ThemeNotFoundException;
import org.derleta.nebula.user.domain.exception.UserNotFoundException;
import org.derleta.nebula.userachievement.domain.exception.UserAchievementNotFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpired(TokenExpiredException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "TOKEN_EXPIRED",
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MissingHeaderException.class
    })
    public ResponseEntity<ErrorResponse> handleIllegalArgument(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler({
            AccountNotFoundException.class,
            GameNotFoundException.class,
            GenderNotFoundException.class,
            NationalityNotFoundException.class,
            ThemeNotFoundException.class,
            UserNotFoundException.class,
            UserAchievementNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "NOT_FOUND",
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(GameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(GameAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "CONFLICT",
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler(HttpRequestException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequest(HttpRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "BAD_GATEWAY",
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(errorResponse);
    }
}

