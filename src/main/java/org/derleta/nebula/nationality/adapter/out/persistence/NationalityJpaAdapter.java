package org.derleta.nebula.nationality.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.derleta.nebula.nationality.adapter.out.persistence.jpa.NationalityJpaRepository;
import org.derleta.nebula.nationality.adapter.out.persistence.mapper.NationalityPersistenceMapper;
import org.derleta.nebula.nationality.application.port.out.NationalityRepositoryPort;
import org.derleta.nebula.nationality.domain.model.Nationality;

import java.util.List;
import java.util.Optional;

/** JPA adapter implementing the NationalityRepositoryPort output port. */
@Component
@RequiredArgsConstructor
public class NationalityJpaAdapter implements NationalityRepositoryPort {

    private final NationalityJpaRepository jpaRepository;

    @Override
    public Optional<Nationality> findById(int id) {
        return jpaRepository.findById(id).map(NationalityPersistenceMapper::toDomain);
    }

    @Override
    public List<Nationality> findAll() {
        return jpaRepository.findAll().stream()
                .map(NationalityPersistenceMapper::toDomain)
                .toList();
    }
}

