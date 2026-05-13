package org.derleta.nebula.nationality.application.port.out;

import org.derleta.nebula.nationality.domain.model.Nationality;

import java.util.List;
import java.util.Optional;

/** Output port — abstraction over the nationality persistence layer. */
public interface NationalityRepositoryPort {
    Optional<Nationality> findById(int id);
    List<Nationality> findAll();
}

