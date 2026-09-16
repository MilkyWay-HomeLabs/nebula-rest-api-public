package org.derleta.nebula.account.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
public final class PasswordUpdateRequest {

    private long userId;
    private String email;
    @NotNull
    @Length(min = 5, max = 64)
    private String actualPassword;

    @NotNull
    @Length(min = 5, max = 64)
    private String newPassword;

}
