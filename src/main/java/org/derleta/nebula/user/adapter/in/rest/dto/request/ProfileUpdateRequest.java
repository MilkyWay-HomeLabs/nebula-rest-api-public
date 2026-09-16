package org.derleta.nebula.user.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.sql.Date;

/** REST request DTO for profile update. */
@Getter
@Builder
@ToString
public final class ProfileUpdateRequest {

    @NotNull
    long userId;
    String firstName;
    String lastName;
    Date birthdate;
    int nationalityId;
    int genderId;
}

