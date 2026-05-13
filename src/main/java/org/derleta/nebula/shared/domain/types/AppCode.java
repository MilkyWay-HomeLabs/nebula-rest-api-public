package org.derleta.nebula.shared.domain.types;

import lombok.Getter;
import lombok.ToString;

/**
 * Enum representing application codes for various system modules.
 * Each enum constant is associated with an identifier and a descriptive name.
 * This can be used to categorize and identify specific parts of the application.
 */
@ToString
@Getter
public enum AppCode {

    ANDROMEDA_AUTH_SERVER(1, "Andromeda Authorization Server"),
    NEBULA_REST_API(2, "Nebula REST API");

    private final int id;
    private final String name;

    AppCode(int id, String name) {
        this.id = id;
        this.name = name;
    }

}
