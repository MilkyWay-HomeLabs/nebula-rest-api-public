package org.derleta.nebula.nationality.adapter.in.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.derleta.nebula.nationality.adapter.in.rest.assembler.NationalityModelAssembler;
import org.derleta.nebula.nationality.adapter.in.rest.dto.NationalityResponse;
import org.derleta.nebula.nationality.adapter.in.rest.dto.RegionResponse;
import org.derleta.nebula.nationality.application.port.in.GetAllNationalitiesUseCase;
import org.derleta.nebula.nationality.application.port.in.GetNationalityUseCase;
import org.derleta.nebula.nationality.domain.model.Nationality;
import org.derleta.nebula.nationality.domain.model.Region;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NationalityControllerTest {

    @Mock
    private GetNationalityUseCase getNationalityUseCase;

    @Mock
    private GetAllNationalitiesUseCase getAllNationalitiesUseCase;

    @Mock
    private NationalityModelAssembler modelAssembler;

    @InjectMocks
    private NationalityController nationalityController;

    private Region region;
    private Nationality nationality1;
    private Nationality nationality2;
    private NationalityResponse natResponse1;
    private NationalityResponse natResponse2;

    @BeforeEach
    void setUp() {
        region = new Region(1, "Europe");
        nationality1 = new Nationality(1, "Polish", "PL", region);
        nationality2 = new Nationality(2, "German", "DE", region);

        RegionResponse regionResponse = RegionResponse.builder().id(1).name("Europe").build();
        natResponse1 = NationalityResponse.builder().id(1).name("Polish").code("PL").region(regionResponse).build();
        natResponse2 = NationalityResponse.builder().id(2).name("German").code("DE").region(regionResponse).build();
    }

    @Test
    void get_validId_returnsNationalityResponse() {
        when(getNationalityUseCase.getNationality(1)).thenReturn(nationality1);
        when(modelAssembler.toModel(nationality1)).thenReturn(natResponse1);

        ResponseEntity<NationalityResponse> response = nationalityController.get(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(natResponse1, response.getBody());
        verify(getNationalityUseCase).getNationality(1);
        verify(modelAssembler).toModel(nationality1);
    }

    @Test
    void getAll_returnsAllNationalityResponses() {
        List<Nationality> nationalities = List.of(nationality1, nationality2);
        when(getAllNationalitiesUseCase.getAllNationalities()).thenReturn(nationalities);

        org.springframework.hateoas.CollectionModel<NationalityResponse> collectionModel =
                org.springframework.hateoas.CollectionModel.of(List.of(natResponse1, natResponse2));
        when(modelAssembler.toCollectionModel(nationalities)).thenReturn(collectionModel);

        ResponseEntity<Collection<NationalityResponse>> response = nationalityController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(getAllNationalitiesUseCase).getAllNationalities();
    }
}

