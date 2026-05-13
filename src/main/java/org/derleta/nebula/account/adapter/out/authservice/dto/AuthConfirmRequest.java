package org.derleta.nebula.account.adapter.out.authservice.dto;

/**
 * Outbound DTO — carries the confirmation token data sent to the auth service.
 * Replaces the inbound {@code UserConfirmationRequest} that was incorrectly used
 * inside the outbound adapter.
 */
public record AuthConfirmRequest(long tokenId, String token) {}

