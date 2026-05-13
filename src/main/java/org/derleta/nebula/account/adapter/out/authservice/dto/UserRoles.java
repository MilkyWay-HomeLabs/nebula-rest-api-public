package org.derleta.nebula.account.adapter.out.authservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Set;
import org.derleta.nebula.shared.security.model.Role;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRoles implements Serializable {

    @JsonProperty("user")
    UserAccount user;
    @JsonProperty("roles")
    Set<Role> roles;

}
