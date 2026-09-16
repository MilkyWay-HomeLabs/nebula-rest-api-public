package org.derleta.nebula.account.domain.types;

import org.derleta.nebula.shared.domain.types.AppCode;

import lombok.Getter;
import lombok.ToString;

/**
 * AccountResponseType is an enumeration representing a variety of response codes
 * and messages related to account processes. These response codes are used to indicate
 * the outcome of specific operations within the system, categorized by account process types
 * like user registration, token confirmation, unlocking accounts, resetting passwords,
 * and changing passwords.
 * <p>
 * Each response type is associated with:
 * - An integer ID representing a unique code.
 * - An application code (AppCode) identifying the related application context.
 * - The process type (AccountProcessType) categorizing the operation being performed.
 * - A descriptive information message detailing the response type.
 * <p>
 * This enumeration helps maintain consistency and readability when handling different
 * account lifecycle stages throughout the system.
 */
@Getter
@ToString
public enum AccountResponseType {

    // registration account codes
    BAD_REGISTRATION_PROCESS_INSTANCE(101, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.USER_REGISTRATION, "Internal Server Error, bad AccountProcess instance."),
    EMAIL_IS_NOT_UNIQUE(102, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.USER_REGISTRATION, "This email address is already in use."),
    LOGIN_IS_NOT_UNIQUE(103, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.USER_REGISTRATION, "Internal Server Error, bad AccountProcess instance."),
    UNIQUE_LOGIN_AND_EMAIL(104, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.USER_REGISTRATION, "Internal Server Error, bad AccountProcess instance."),
    BAD_REGISTRATION_REQUEST_TYPE(105, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.USER_REGISTRATION, "Bad request type."),
    VERIFICATION_MAIL_FROM_REGISTRATION(106, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.USER_REGISTRATION, "Verification mail was sent."),
    NEBULA_BAD_REGISTRATION_RESPONSE_INSTANCE(151, AppCode.NEBULA_REST_API,
            AccountProcessType.USER_REGISTRATION, "Bad authorization response instance, please contact system administrator to resolver problem."),
    NEBULA_BAD_REGISTRATION_REQUEST_INSTANCE(152, AppCode.NEBULA_REST_API,
            AccountProcessType.USER_REGISTRATION, "Bad request instance."),
    USER_CREATED_IN_NEBULA_DB(154, AppCode.NEBULA_REST_API,
            AccountProcessType.USER_REGISTRATION, "User created in nebula database."),
    USER_NOT_CREATED_IN_NEBULA_DB(155, AppCode.NEBULA_REST_API,
            AccountProcessType.USER_REGISTRATION, "User was not created in Nebula database"),
    USER_SETTINGS_TABLES_CREATED_NEBULA_DB(157, AppCode.NEBULA_REST_API,
            AccountProcessType.USER_REGISTRATION, "Users settings tables and user_games created Nebula database"),
    NEBULA_INVALID_EMAIL(158, AppCode.NEBULA_REST_API,
            AccountProcessType.USER_REGISTRATION, "Invalid email address"),
    NEBULA_INVALID_ENCRYPTED_PASSWORD(159, AppCode.NEBULA_REST_API,
            AccountProcessType.USER_REGISTRATION, "Encrypted password has invalid format"),
    // confirmation account codes
    TOKEN_NOT_FOUND(201, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.CONFIRMATION_TOKEN, "Token not found."),
    INVALID_TOKEN_VALUE(202, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.CONFIRMATION_TOKEN, "Invalid token value."),
    TOKEN_EXPIRED(203, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.CONFIRMATION_TOKEN, "Token expired."),
    TOKEN_IS_VALID(204, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.CONFIRMATION_TOKEN, "Token is valid."),
    BAD_CONFIRMATION_REQUEST_TYPE(205, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.CONFIRMATION_TOKEN, "Bad request type."),
    ACCOUNT_CONFIRMED(206, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.CONFIRMATION_TOKEN, "Account confirmed."),
    // unlock account codes
    BAD_UNLOCK_PROCESS_INSTANCE(301, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.UNLOCK_ACCOUNT, "Internal Server Error, bad AccountProcess instance."),
    VERIFICATION_MAIL_FROM_UNLOCK(302, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.UNLOCK_ACCOUNT, "Verification mail was sent."),
    ACCOUNT_NOT_EXIST_UNLOCK_ACCOUNT(303, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.UNLOCK_ACCOUNT, "Account not exist."),
    ACCOUNT_VERIFIED_AND_NOT_BLOCKED(304, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.UNLOCK_ACCOUNT, "Account is verified and not blocked."),
    ACCOUNT_CAN_BE_UNLOCKED(305, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.UNLOCK_ACCOUNT, "Account can be unlocked."),
    BAD_UNLOCK_REQUEST_TYPE(306, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.UNLOCK_ACCOUNT, "Bad request type."),
    BAD_UNLOCK_HTTP_REQUEST(307, AppCode.NEBULA_REST_API,
            AccountProcessType.UNLOCK_ACCOUNT, "Bad http request."),
    // password reset for account codes
    BAD_RESET_PASSWD_PROCESS_INSTANCE(401, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.RESET_PASSWORD, "Internal Server Error, bad AccountProcess instance."),
    BAD_USER_ENTITY_INSTANCE(402, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.RESET_PASSWORD, "Password was not sent, not instance of UserEntityDecrypted."),
    BAD_RESET_PASSWD_REQUEST_TYPE(403, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.RESET_PASSWORD, "Bad request type."),
    MAIL_NEW_PASSWD_SENT(404, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.RESET_PASSWORD, "New password mail was sent."),
    ACCOUNT_NOT_EXIST_RESET_PASSWD(405, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.RESET_PASSWORD, "Account not exist."),
    ACCOUNT_IS_BLOCKED(406, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.RESET_PASSWORD, "Account is blocked, unlock it first."),
    ACCOUNT_IS_NOT_VERIFIED(407, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.RESET_PASSWORD, "Account is not verified, verify account first."),
    PASSWORD_CAN_BE_GENERATED(408, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.RESET_PASSWORD, "Password can be generated."),
    PASSWORD_RESET_ACCESS_TOKEN_EXPIRED(601, AppCode.NEBULA_REST_API, AccountProcessType.RESET_PASSWORD, "Access token expired"),

    // change password for account codes
    BAD_CHANGE_PASSWD_REQUEST_TYPE(501, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.CHANGE_PASSWORD, "Bad request type."),
    EMAIL_NOT_EXIST_CHANGE_PASSWD(502, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.CHANGE_PASSWORD, "Bad email address."),
    ACCOUNT_IS_BLOCKED_CHANGE_PASSWD(503, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.CHANGE_PASSWORD, "Account is blocked, unlock it first."),
    BAD_ACTUAL_PASSWORD_CHANGE_PASSWD(504, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.CHANGE_PASSWORD, "Bad actual password."),
    PASSWORD_CAN_BE_CHANGED(505, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.CHANGE_PASSWORD, "Password can be changed."),
    PASSWORD_CHANGED(506, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.CHANGE_PASSWORD, "Password  changed."),
    PASSWORD_NOT_CHANGED(507, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.CHANGE_PASSWORD, "Password not changed."),
    PASSWORD_CHANGED_BUT_MAIL_NOT_SEND(507, AppCode.ANDROMEDA_AUTH_SERVER,
            AccountProcessType.CHANGE_PASSWORD, "Password was changed but probably information mail wasn't send."),
    PASSWORD_CHANGE_ACCESS_TOKEN_EXPIRED(601, AppCode.NEBULA_REST_API, AccountProcessType.CHANGE_PASSWORD, "Access token expired"),
    // multiple account codes

    // general codes
    INTERNAL_SERVER_ERROR(500, AppCode.NEBULA_REST_API, null, "Internal server error"),
    UNAUTHORIZED(401, AppCode.NEBULA_REST_API, null, "unauthorized"),

    NULL(0, null, null, "null");

    private final int id;
    private final AppCode appCode;
    private final AccountProcessType processType;
    private final String info;

    AccountResponseType(int id, AppCode appCode, AccountProcessType processType, String info) {
        this.id = id;
        this.info = info;
        this.appCode = appCode;
        this.processType = processType;
    }

}
