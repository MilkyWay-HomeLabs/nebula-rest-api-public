package org.derleta.nebula.nationality.adapter.out.persistence;

import org.derleta.nebula.nationality.adapter.out.persistence.entity.NationalityJpaEntity;
import org.derleta.nebula.nationality.adapter.out.persistence.entity.RegionJpaEntity;
import org.derleta.nebula.nationality.adapter.out.persistence.jpa.NationalityJpaRepository;
import org.derleta.nebula.nationality.domain.model.Nationality;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NationalityJpaAdapterTest {

    @Mock private NationalityJpaRepository jpaRepository;
    @InjectMocks private NationalityJpaAdapter adapter;

    private NationalityJpaEntity entity(int id, String name, String code) {
        RegionJpaEntity region = new RegionJpaEntity(1, "Europe");
        return new NationalityJpaEntity(id, name, code, region);
    }

    @Test
    void findById_found_returnsNationality() {
        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity(1, "Polish", "POL")));
        Optional<Nationality> result = adapter.findById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().id());
        assertEquals("Polish", result.get().name());
        assertEquals("POL", result.get().code());
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(jpaRepository.findById(99)).thenReturn(Optional.empty());
        assertTrue(adapter.findById(99).isEmpty());
    }

    @Test
    void findAll_returnsMappedList() {
        when(jpaRepository.findAll()).thenReturn(List.of(
                entity(1, "Polish", "POL"), entity(2, "German", "DEU")));
        List<Nationality> result = adapter.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void findAll_empty_returnsEmptyList() {
        when(jpaRepository.findAll()).thenReturn(List.of());
        assertTrue(adapter.findAll().isEmpty());
    }
}
