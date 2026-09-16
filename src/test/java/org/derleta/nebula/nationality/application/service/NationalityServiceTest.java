package org.derleta.nebula.nationality.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.derleta.nebula.nationality.application.port.out.NationalityRepositoryPort;
import org.derleta.nebula.nationality.domain.exception.NationalityNotFoundException;
import org.derleta.nebula.nationality.domain.model.Nationality;
import org.derleta.nebula.nationality.domain.model.Region;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NationalityServiceTest {

    @Mock
    private NationalityRepositoryPort nationalityRepository;

    @InjectMocks
    private NationalityService nationalityService;

    private final Region region = new Region(1, "Europe");

    @Test
    void getNationality_existingId_returnsNationality() {
        Nationality nationality = new Nationality(1, "Polish", "PL", region);
        when(nationalityRepository.findById(1)).thenReturn(Optional.of(nationality));

        Nationality result = nationalityService.getNationality(1);

        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("Polish", result.name());
        assertEquals("PL", result.code());
        assertEquals(region, result.region());
        verify(nationalityRepository).findById(1);
    }

    @Test
    void getNationality_missingId_throwsNationalityNotFoundException() {
        when(nationalityRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NationalityNotFoundException.class, () -> nationalityService.getNationality(99));
        verify(nationalityRepository).findById(99);
    }

    @Test
    void getAllNationalities_returnsAllNationalities() {
        List<Nationality> nationalities = List.of(
                new Nationality(1, "Polish", "PL", region),
                new Nationality(2, "German", "DE", region)
        );
        when(nationalityRepository.findAll()).thenReturn(nationalities);

        List<Nationality> result = nationalityService.getAllNationalities();

        assertEquals(2, result.size());
        verify(nationalityRepository).findAll();
    }
}

