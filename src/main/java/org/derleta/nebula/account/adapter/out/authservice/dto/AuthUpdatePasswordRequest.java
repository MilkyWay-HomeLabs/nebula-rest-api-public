package org.derleta.nebula.account.adapter.out.authservice.dto;

/**
 * Outbound DTO — carries password change data sent to the auth service.
 * Replaces the inbound {@code PasswordUpdateRequest} that was incorrectly used
 * inside the outbound adapter.
 */
public record AuthUpdatePasswordRequest(
        long userId,
        String email,
        String actualPassword,
        String newPassword) {}

