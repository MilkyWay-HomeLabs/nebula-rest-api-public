package org.derleta.nebula.token.domain.types;

import org.derleta.nebula.shared.domain.types.AppCode;

import lombok.Getter;
import lombok.ToString;

/**
 * Enum representing response types for various token-related operations.
 */
@Getter
@ToString
public enum TokenResponseType {

    //    Any Tokens codes
    TOKEN_EXPIRED(101, AppCode.NEBULA_REST_API, "Token expired."),
    //   Confirmation token codes
    CONFIRMATION_TOKEN_EXPIRED(201, AppCode.NEBULA_REST_API, "Confirmation token expired."),
    //    Access Token codes
    ACCESS_TOKEN_EXPIRED(301, AppCode.NEBULA_REST_API, "Access token expired."),
    //    Refresh Token codes

    ;

    private final int id;
    private final AppCode appCode;
    private final String info;

    TokenResponseType(int id, AppCode appCode, String info) {
        this.id = id;
        this.appCode = appCode;
        this.info = info;
    }

}
