package org.derleta.nebula.shared.security.model;

import lombok.*;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TokenData {

    boolean valid;
    long userId;
    String email;
    String token;
    Set<Role> roles;

}
