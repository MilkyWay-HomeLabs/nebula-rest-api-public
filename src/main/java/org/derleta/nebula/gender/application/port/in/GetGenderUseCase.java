package org.derleta.nebula.gender.application.port.in;

import org.derleta.nebula.gender.domain.model.Gender;

/** Input port — retrieve a single gender by id. */
public interface GetGenderUseCase {
    Gender getGender(int id);
}

