package org.derleta.nebula.gender.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.derleta.nebula.gender.adapter.out.persistence.jpa.GenderJpaRepository;
import org.derleta.nebula.gender.adapter.out.persistence.mapper.GenderPersistenceMapper;
import org.derleta.nebula.gender.application.port.out.GenderRepositoryPort;
import org.derleta.nebula.gender.domain.model.Gender;

import java.util.List;
import java.util.Optional;

/** JPA adapter implementing the GenderRepositoryPort output port. */
@Component
@RequiredArgsConstructor
public class GenderJpaAdapter implements GenderRepositoryPort {

    private final GenderJpaRepository jpaRepository;

    @Override
    public Optional<Gender> findById(int id) {
        return jpaRepository.findById(id).map(GenderPersistenceMapper::toDomain);
    }

    @Override
    public List<Gender> findAll() {
        return jpaRepository.findAll().stream()
                .map(GenderPersistenceMapper::toDomain)
                .toList();
    }
}

