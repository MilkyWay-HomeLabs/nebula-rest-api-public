package org.derleta.nebula.account.domain.types;

import lombok.Getter;
import lombok.ToString;

/**
 * Enum representing various processes related to an account lifecycle within the system.
 * Each process type is associated with an identifier and a descriptive name, which can
 * be used to categorize and identify specific operations performed on an account.
 * <p>
 * The enum constants include:
 * - USER_REGISTRATION: Represents user registration process.
 * - UNLOCK_ACCOUNT: Represents the account unlocking process.
 * - CONFIRMATION_TOKEN: Refers to token confirmation during the account verification process.
 * - RESET_PASSWORD: Represents the password reset process for an account.
 * - CHANGE_PASSWORD: Refers to changing the password for an existing account.
 */
@Getter
@ToString
public enum AccountProcessType {

    USER_REGISTRATION(1, "UserRegistration"),
    UNLOCK_ACCOUNT(2, "UnlockAccount"),
    CONFIRMATION_TOKEN(3, "ConfirmationToken"),
    RESET_PASSWORD(4, "ResetPassword"),
    CHANGE_PASSWORD(5, "ChangePassword");

    private final int id;
    private final String name;

    AccountProcessType(int id, String name) {
        this.id = id;
        this.name = name;
    }

}
