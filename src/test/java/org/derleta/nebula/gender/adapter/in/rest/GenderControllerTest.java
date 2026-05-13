package org.derleta.nebula.gender.adapter.in.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.derleta.nebula.gender.adapter.in.rest.assembler.GenderModelAssembler;
import org.derleta.nebula.gender.adapter.in.rest.dto.GenderResponse;
import org.derleta.nebula.gender.application.port.in.GetAllGendersUseCase;
import org.derleta.nebula.gender.application.port.in.GetGenderUseCase;
import org.derleta.nebula.gender.domain.model.Gender;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenderControllerTest {

    @Mock
    private GetGenderUseCase getGenderUseCase;

    @Mock
    private GetAllGendersUseCase getAllGendersUseCase;

    @Mock
    private GenderModelAssembler modelAssembler;

    @InjectMocks
    private GenderController genderController;

    private Gender gender1;
    private Gender gender2;
    private GenderResponse response1;
    private GenderResponse response2;

    @BeforeEach
    void setUp() {
        gender1 = new Gender(1, "Male");
        gender2 = new Gender(2, "Female");
        response1 = GenderResponse.builder().id(1).name("Male").imgURL("https://milkyway.test/resources/nebula/icon/gender/1.png").build();
        response2 = GenderResponse.builder().id(2).name("Female").imgURL("https://milkyway.test/resources/nebula/icon/gender/2.png").build();
    }

    @Test
    void get_validId_returnsGenderResponse() {
        when(getGenderUseCase.getGender(1)).thenReturn(gender1);
        when(modelAssembler.toModel(gender1)).thenReturn(response1);

        ResponseEntity<GenderResponse> response = genderController.get(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response1, response.getBody());
        verify(getGenderUseCase).getGender(1);
        verify(modelAssembler).toModel(gender1);
    }

    @Test
    void getAll_returnsAllGenderResponses() {
        List<Gender> genders = List.of(gender1, gender2);
        when(getAllGendersUseCase.getAllGenders()).thenReturn(genders);

        org.springframework.hateoas.CollectionModel<GenderResponse> collectionModel =
                org.springframework.hateoas.CollectionModel.of(List.of(response1, response2));
        when(modelAssembler.toCollectionModel(genders)).thenReturn(collectionModel);

        ResponseEntity<Collection<GenderResponse>> response = genderController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(getAllGendersUseCase).getAllGenders();
    }
}

