package org.derleta.nebula.gender.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.derleta.nebula.gender.application.port.out.GenderRepositoryPort;
import org.derleta.nebula.gender.domain.exception.GenderNotFoundException;
import org.derleta.nebula.gender.domain.model.Gender;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenderServiceTest {

    @Mock
    private GenderRepositoryPort genderRepository;

    @InjectMocks
    private GenderService genderService;

    @Test
    void getGender_existingId_returnsGender() {
        Gender gender = new Gender(1, "Male");
        when(genderRepository.findById(1)).thenReturn(Optional.of(gender));

        Gender result = genderService.getGender(1);

        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("Male", result.name());
        verify(genderRepository).findById(1);
    }

    @Test
    void getGender_missingId_throwsGenderNotFoundException() {
        when(genderRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(GenderNotFoundException.class, () -> genderService.getGender(99));
        verify(genderRepository).findById(99);
    }

    @Test
    void getAllGenders_returnsAllGenders() {
        List<Gender> genders = List.of(new Gender(1, "Male"), new Gender(2, "Female"));
        when(genderRepository.findAll()).thenReturn(genders);

        List<Gender> result = genderService.getAllGenders();

        assertEquals(2, result.size());
        verify(genderRepository).findAll();
    }
}

