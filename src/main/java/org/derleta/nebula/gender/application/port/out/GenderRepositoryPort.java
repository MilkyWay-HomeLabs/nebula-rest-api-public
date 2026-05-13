package org.derleta.nebula.gender.application.port.out;

import org.derleta.nebula.gender.domain.model.Gender;

import java.util.List;
import java.util.Optional;

/** Output port — abstraction over the gender persistence layer. */
public interface GenderRepositoryPort {
    Optional<Gender> findById(int id);
    List<Gender> findAll();
}

