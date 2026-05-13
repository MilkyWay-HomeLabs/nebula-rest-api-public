package org.derleta.nebula.account.application.port.in.command;

import java.util.Date;

/**
 * Command carrying all data needed to register a new account.
 */
public record RegisterAccountCommand(
        String login, String email, String password,
        Date birthDate, int nationalityId, int genderId) {
}
