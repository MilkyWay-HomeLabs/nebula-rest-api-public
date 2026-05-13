package org.derleta.nebula.account.adapter.in.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import java.sql.Date;

@Getter
@Builder
@ToString
public final class AccountRegistrationRequest {

    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$")
    String login;

    @NotBlank
    @Email
    @Size(max = 254)
    String email;

    @NotBlank
    @Size(min = 8, max = 128)
    String password;

    Date birthdate;
    Integer nationality;
    Integer gender;

}
