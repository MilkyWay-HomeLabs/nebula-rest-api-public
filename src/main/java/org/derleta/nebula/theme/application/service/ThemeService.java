package org.derleta.nebula.theme.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.derleta.nebula.theme.domain.model.Theme;
import org.derleta.nebula.theme.application.port.in.GetAllThemesUseCase;
import org.derleta.nebula.theme.application.port.in.GetThemeUseCase;
import org.derleta.nebula.theme.application.port.out.ThemeRepositoryPort;
import org.derleta.nebula.theme.domain.exception.ThemeNotFoundException;

import java.util.List;

/** Application service implementing theme query use cases. */
@Service
@RequiredArgsConstructor
public class ThemeService implements GetThemeUseCase, GetAllThemesUseCase {

    private final ThemeRepositoryPort themeRepository;

    @Override
    public Theme getTheme(int id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new ThemeNotFoundException("Theme with id: " + id + " not found"));
    }

    @Override
    public List<Theme> getAllThemes() {
        return themeRepository.findAll();
    }
}

