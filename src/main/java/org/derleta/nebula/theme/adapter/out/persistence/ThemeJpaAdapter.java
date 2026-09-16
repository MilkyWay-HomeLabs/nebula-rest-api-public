package org.derleta.nebula.theme.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.derleta.nebula.theme.adapter.out.persistence.mapper.ThemeMapper;
import org.derleta.nebula.theme.domain.model.Theme;
import org.derleta.nebula.theme.adapter.out.persistence.jpa.ThemeJpaRepository;
import org.derleta.nebula.theme.application.port.out.ThemeRepositoryPort;

import java.util.List;
import java.util.Optional;

/** Outbound persistence adapter implementing ThemeRepositoryPort. */
@Component
@RequiredArgsConstructor
public class ThemeJpaAdapter implements ThemeRepositoryPort {

    private final ThemeJpaRepository repository;

    @Override
    public Optional<Theme> findById(int id) {
        return repository.findById(id).map(ThemeMapper::toTheme);
    }

    @Override
    public List<Theme> findAll() {
        return ThemeMapper.toThemes(repository.findAll());
    }
}

