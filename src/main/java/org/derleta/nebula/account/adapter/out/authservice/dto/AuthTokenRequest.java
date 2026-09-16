package org.derleta.nebula.account.adapter.out.authservice.dto;

/**
 * Outbound DTO — carries credentials for token generation sent to the auth service.
 * Replaces the inbound {@code AuthEmailRequest} that was incorrectly used inside
 * the outbound adapter.
 */
public record AuthTokenRequest(String email, String password) {}

