package org.derleta.nebula.token.adapter.in.rest.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.derleta.nebula.shared.adapter.in.rest.dto.Response;
import org.derleta.nebula.shared.security.model.Role;

import java.util.Set;

/** REST response DTO carrying JWT token metadata. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TokenDataResponse extends RepresentationModel<TokenDataResponse> implements Response {

    boolean valid;
    long userId;
    String email;
    String token;
    Set<Role> roles;
}

