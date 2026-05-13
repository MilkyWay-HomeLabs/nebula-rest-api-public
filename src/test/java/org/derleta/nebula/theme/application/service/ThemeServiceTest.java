package org.derleta.nebula.theme.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.derleta.nebula.theme.domain.model.Theme;
import org.derleta.nebula.theme.application.port.out.ThemeRepositoryPort;
import org.derleta.nebula.theme.domain.exception.ThemeNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThemeServiceTest {

    @Mock
    private ThemeRepositoryPort themeRepository;

    @InjectMocks
    private ThemeService themeService;

    @Test
    void getTheme_existingId_returnsTheme() {
        Theme theme = new Theme(1, "Dark");
        when(themeRepository.findById(1)).thenReturn(Optional.of(theme));

        Theme result = themeService.getTheme(1);

        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("Dark", result.name());
        verify(themeRepository).findById(1);
    }

    @Test
    void getTheme_missingId_throwsThemeNotFoundException() {
        when(themeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ThemeNotFoundException.class, () -> themeService.getTheme(99));
        verify(themeRepository).findById(99);
    }

    @Test
    void getAllThemes_returnsAllThemes() {
        List<Theme> themes = List.of(new Theme(1, "Dark"), new Theme(2, "Light"));
        when(themeRepository.findAll()).thenReturn(themes);

        List<Theme> result = themeService.getAllThemes();

        assertEquals(2, result.size());
        verify(themeRepository).findAll();
    }

    @Test
    void getAllThemes_emptyRepository_returnsEmptyList() {
        when(themeRepository.findAll()).thenReturn(List.of());

        List<Theme> result = themeService.getAllThemes();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(themeRepository).findAll();
    }
}

