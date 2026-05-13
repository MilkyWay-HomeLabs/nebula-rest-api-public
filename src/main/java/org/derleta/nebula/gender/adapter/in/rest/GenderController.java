package org.derleta.nebula.gender.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.derleta.nebula.gender.adapter.in.rest.assembler.GenderModelAssembler;
import org.derleta.nebula.gender.adapter.in.rest.dto.GenderResponse;
import org.derleta.nebula.gender.application.port.in.GetAllGendersUseCase;
import org.derleta.nebula.gender.application.port.in.GetGenderUseCase;

import java.util.Collection;

/** REST adapter — exposes read-only gender endpoints. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class GenderController {

    public static final String DEFAULT_PATH = "genders";

    private final GetGenderUseCase getGenderUseCase;
    private final GetAllGendersUseCase getAllGendersUseCase;
    private final GenderModelAssembler modelAssembler;

    @GetMapping(value = "/" + DEFAULT_PATH + "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<GenderResponse> get(@PathVariable Integer id) {
        return ResponseEntity.ok(modelAssembler.toModel(getGenderUseCase.getGender(id)));
    }

    @GetMapping(value = "/" + DEFAULT_PATH, produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Collection<GenderResponse>> getAll() {
        return ResponseEntity.ok(
                modelAssembler.toCollectionModel(getAllGendersUseCase.getAllGenders()).getContent()
        );
    }
}

