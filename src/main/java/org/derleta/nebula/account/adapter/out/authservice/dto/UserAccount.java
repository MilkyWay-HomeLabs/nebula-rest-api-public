package org.derleta.nebula.account.adapter.out.authservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAccount implements Serializable {

    @JsonProperty("userId")
    long userId;
    @JsonProperty("username")
    String username;
    @JsonProperty("email")
    String email;

}
