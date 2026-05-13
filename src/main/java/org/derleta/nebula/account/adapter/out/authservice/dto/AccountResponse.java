package org.derleta.nebula.account.adapter.out.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.derleta.nebula.shared.adapter.in.rest.dto.Response;
import org.derleta.nebula.account.domain.types.AccountResponseType;

/** Outbound DTO representing a response from the external authentication service. */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public final class AccountResponse implements Response {

    @JsonProperty("success")
    boolean success;

    @JsonProperty("type")
    AccountResponseType type;
}

