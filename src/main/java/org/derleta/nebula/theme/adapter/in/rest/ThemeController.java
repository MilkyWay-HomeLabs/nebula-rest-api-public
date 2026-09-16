package org.derleta.nebula.theme.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.derleta.nebula.theme.adapter.in.rest.assembler.ThemeModelAssembler;
import org.derleta.nebula.theme.adapter.in.rest.dto.ThemeResponse;
import org.derleta.nebula.theme.application.port.in.GetAllThemesUseCase;
import org.derleta.nebula.theme.application.port.in.GetThemeUseCase;

import java.util.Collection;

/** REST adapter — exposes read-only theme endpoints. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ThemeController {

    public static final String DEFAULT_PATH = "themes";

    private final GetThemeUseCase getThemeUseCase;
    private final GetAllThemesUseCase getAllThemesUseCase;
    private final ThemeModelAssembler modelAssembler;

    @GetMapping(value = "/" + DEFAULT_PATH + "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<ThemeResponse> get(@PathVariable Integer id) {
        return ResponseEntity.ok(modelAssembler.toModel(getThemeUseCase.getTheme(id)));
    }

    @GetMapping(value = "/" + DEFAULT_PATH, produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Collection<ThemeResponse>> getAll() {
        return ResponseEntity.ok(
                modelAssembler.toCollectionModel(getAllThemesUseCase.getAllThemes()).getContent()
        );
    }
}

