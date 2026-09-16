package org.derleta.nebula.nationality.application.port.in;

import org.derleta.nebula.nationality.domain.model.Nationality;

import java.util.List;

/** Input port — retrieve all available nationalities. */
public interface GetAllNationalitiesUseCase {
    List<Nationality> getAllNationalities();
}

