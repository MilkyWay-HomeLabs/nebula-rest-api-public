package org.derleta.nebula.theme.adapter.in.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.derleta.nebula.theme.domain.model.Theme;
import org.derleta.nebula.theme.adapter.in.rest.assembler.ThemeModelAssembler;
import org.derleta.nebula.theme.adapter.in.rest.dto.ThemeResponse;
import org.derleta.nebula.theme.application.port.in.GetAllThemesUseCase;
import org.derleta.nebula.theme.application.port.in.GetThemeUseCase;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThemeControllerTest {

    @Mock
    private GetThemeUseCase getThemeUseCase;

    @Mock
    private GetAllThemesUseCase getAllThemesUseCase;

    @Mock
    private ThemeModelAssembler modelAssembler;

    @InjectMocks
    private ThemeController themeController;

    private Theme darkTheme;
    private Theme lightTheme;
    private ThemeResponse darkResponse;
    private ThemeResponse lightResponse;

    @BeforeEach
    void setUp() {
        darkTheme = new Theme(1, "Dark");
        lightTheme = new Theme(2, "Light");
        darkResponse = ThemeResponse.builder().id(1).name("Dark").imgURL("https://milkyway.test/resources/nebula/icon/theme/1.png").build();
        lightResponse = ThemeResponse.builder().id(2).name("Light").imgURL("https://milkyway.test/resources/nebula/icon/theme/2.png").build();
    }

    @Test
    void get_validId_returnsThemeResponse() {
        when(getThemeUseCase.getTheme(1)).thenReturn(darkTheme);
        when(modelAssembler.toModel(darkTheme)).thenReturn(darkResponse);

        ResponseEntity<ThemeResponse> response = themeController.get(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(darkResponse, response.getBody());
        verify(getThemeUseCase).getTheme(1);
        verify(modelAssembler).toModel(darkTheme);
    }

    @Test
    void getAll_returnsAllThemes() {
        List<Theme> themes = List.of(darkTheme, lightTheme);
        when(getAllThemesUseCase.getAllThemes()).thenReturn(themes);
        when(modelAssembler.toCollectionModel(themes))
                .thenReturn(CollectionModel.of(List.of(darkResponse, lightResponse)));

        ResponseEntity<Collection<ThemeResponse>> response = themeController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(getAllThemesUseCase).getAllThemes();
    }
}

