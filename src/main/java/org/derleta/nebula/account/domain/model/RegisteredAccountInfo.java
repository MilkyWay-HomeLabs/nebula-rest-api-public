package org.derleta.nebula.account.domain.model;


/**
 * Holds the basic identity data returned by the auth service after successful registration.
 */
public record RegisteredAccountInfo(long userId, String username, String email) {
}
