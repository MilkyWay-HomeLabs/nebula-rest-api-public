package org.derleta.nebula.token.adapter.in.rest.mapper;

import org.derleta.nebula.shared.security.model.Role;
import org.derleta.nebula.shared.security.model.TokenData;
import org.derleta.nebula.token.adapter.in.rest.dto.response.TokenDataResponse;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TokenRestMapperTest {

    @Test
    void toResponse_validTokenData_mapsAllFieldsAndAddsSelfLink() {
        Set<Role> roles = Set.of(new Role(1, "USER"), new Role(2, "ADMIN"));
        TokenData entity = new TokenData(true, 123L, "tester@example.com", "token-value", roles);

        TokenDataResponse result = TokenRestMapper.toResponse(entity);

        assertThat(result.isValid()).isTrue();
        assertThat(result.getUserId()).isEqualTo(123L);
        assertThat(result.getEmail()).isEqualTo("tester@example.com");
        assertThat(result.getToken()).isEqualTo("token-value");
        assertThat(result.getRoles()).isEqualTo(roles);
        assertThat(result.getLinks()).isNotEmpty();
        assertThat(result.getLinks().stream().anyMatch(l -> l.getRel().value().equals("self"))).isTrue();
    }

    @Test
    void toResponse_emptyRoles_returnsResponseWithEmptyRoles() {
        TokenData entity = new TokenData(false, 0L, "other@example.com", "tkn", Set.of());

        TokenDataResponse result = TokenRestMapper.toResponse(entity);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getRoles()).isEmpty();
        assertThat(result.getLinks()).isNotEmpty();
    }
}

