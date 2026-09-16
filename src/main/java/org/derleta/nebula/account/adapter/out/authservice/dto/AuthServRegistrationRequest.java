package org.derleta.nebula.account.adapter.out.authservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/** Outbound DTO sent to the external authentication service during user registration. */
@Getter
@Builder
@ToString
public final class AuthServRegistrationRequest {
    String username;
    String email;
    String encryptedPassword;
}

