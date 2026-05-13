package org.derleta.nebula.token.adapter.in.rest.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.shared.security.model.TokenData;
import org.derleta.nebula.token.adapter.in.rest.TokenController;
import org.derleta.nebula.token.adapter.in.rest.dto.response.TokenDataResponse;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * Static REST mapper that converts {@link TokenData} domain objects
 * to {@link TokenDataResponse} DTOs and adds a HATEOAS self-link.
 * Replaces the old TokenDataApiMapper + TokenModelAssembler pair.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenRestMapper {

    /**
     * Converts a {@link TokenData} domain object to a {@link TokenDataResponse}
     * and attaches a HATEOAS self-link pointing to the token endpoint.
     *
     * @param entity the token data to convert
     * @return the response DTO with self-link
     */
    public static TokenDataResponse toResponse(final TokenData entity) {
        TokenDataResponse response = TokenDataResponse.builder()
                .valid(entity.isValid())
                .userId(entity.getUserId())
                .email(entity.getEmail())
                .token(entity.getToken())
                .roles(entity.getRoles())
                .build();
        response.add(
                linkTo(TokenController.class)
                        .slash(TokenController.DEFAULT_PATH)
                        .withSelfRel()
        );
        return response;
    }
}

