package org.derleta.nebula.shared.security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Role implements Serializable {

    @JsonProperty("roleId")
    int roleId;
    @JsonProperty("roleName")
    String roleName;

}
