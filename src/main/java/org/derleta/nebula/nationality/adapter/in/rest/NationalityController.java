package org.derleta.nebula.nationality.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.derleta.nebula.nationality.adapter.in.rest.assembler.NationalityModelAssembler;
import org.derleta.nebula.nationality.adapter.in.rest.dto.NationalityResponse;
import org.derleta.nebula.nationality.application.port.in.GetAllNationalitiesUseCase;
import org.derleta.nebula.nationality.application.port.in.GetNationalityUseCase;

import java.util.Collection;

/** REST adapter — exposes read-only nationality endpoints. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class NationalityController {

    public static final String DEFAULT_PATH = "nationalities";

    private final GetNationalityUseCase getNationalityUseCase;
    private final GetAllNationalitiesUseCase getAllNationalitiesUseCase;
    private final NationalityModelAssembler modelAssembler;

    @GetMapping(value = "/" + DEFAULT_PATH + "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<NationalityResponse> get(@PathVariable Integer id) {
        return ResponseEntity.ok(modelAssembler.toModel(getNationalityUseCase.getNationality(id)));
    }

    @GetMapping(value = "/" + DEFAULT_PATH, produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Collection<NationalityResponse>> getAll() {
        return ResponseEntity.ok(
                modelAssembler.toCollectionModel(getAllNationalitiesUseCase.getAllNationalities()).getContent()
        );
    }
}

