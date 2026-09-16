package org.derleta.nebula.nationality.application.port.in;

import org.derleta.nebula.nationality.domain.model.Nationality;

/** Input port — retrieve a single nationality by id. */
public interface GetNationalityUseCase {
    Nationality getNationality(int id);
}

