package org.derleta.nebula.nationality.domain.model;

/** Read-only reference aggregate for nationality. Region is embedded as a value object. */
public record Nationality(int id, String name, String code, Region region) {}

