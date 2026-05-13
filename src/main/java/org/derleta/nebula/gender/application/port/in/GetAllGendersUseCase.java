package org.derleta.nebula.gender.application.port.in;

import org.derleta.nebula.gender.domain.model.Gender;

import java.util.List;

/** Input port — retrieve all available genders. */
public interface GetAllGendersUseCase {
    List<Gender> getAllGenders();
}

