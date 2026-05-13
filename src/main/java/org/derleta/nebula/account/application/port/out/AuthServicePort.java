package org.derleta.nebula.account.application.port.out;

import org.derleta.nebula.account.application.port.in.command.ChangePasswordCommand;
import org.derleta.nebula.account.domain.model.AccountOperationResult;
import org.derleta.nebula.account.domain.model.RegisteredAccountInfo;
import org.derleta.nebula.account.domain.model.TokenResult;

/**
 * Output port — abstraction over the external authentication service.
 */
public interface AuthServicePort {

    /**
     * Registers a new account on the auth service. Plain password — adapter handles encryption.
     */
    AccountOperationResult register(String username, String email, String password);

    /**
     * Confirms account using the token id and token value from the confirmation email.
     */
    AccountOperationResult confirm(long tokenId, String token);

    /**
     * Sends an unlock email for the account with the given id.
     */
    AccountOperationResult unlock(long accountId);

    /**
     * Sends a password reset email for the account with the given email address.
     */
    AccountOperationResult resetPassword(String email);

    /**
     * Generates access and refresh tokens for the given credentials.
     */
    TokenResult generateToken(String email, String password);

    /**
     * Updates the account password; jwtToken is used for bearer authentication on the auth service.
     */
    AccountOperationResult updatePassword(String jwtToken, ChangePasswordCommand command);

    /**
     * Retrieves registered account info (userId, username, email) after successful registration.
     */
    RegisteredAccountInfo getAccount(String username, String email);
}
