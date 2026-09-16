package org.derleta.nebula.account.adapter.in.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.derleta.nebula.account.domain.types.AccountResponseType;

/** REST response DTO for all non-token account write operations. */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public final class AccountOperationResponse {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("type")
    private AccountResponseType type;
}
