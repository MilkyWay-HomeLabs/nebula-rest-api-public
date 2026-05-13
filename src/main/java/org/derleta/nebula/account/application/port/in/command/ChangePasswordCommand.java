package org.derleta.nebula.account.application.port.in.command;

/**
 * Command carrying all data needed to change a password.
 */
public record ChangePasswordCommand(
        long userId, String email,
        String actualPassword, String newPassword) {
}
