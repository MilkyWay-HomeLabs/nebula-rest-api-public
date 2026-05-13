package org.derleta.nebula.gender.adapter.out.persistence;

import org.derleta.nebula.gender.adapter.out.persistence.entity.GenderJpaEntity;
import org.derleta.nebula.gender.adapter.out.persistence.jpa.GenderJpaRepository;
import org.derleta.nebula.gender.domain.model.Gender;
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
class GenderJpaAdapterTest {

    @Mock private GenderJpaRepository jpaRepository;
    @InjectMocks private GenderJpaAdapter adapter;

    private GenderJpaEntity entity(int id, String name) {
        GenderJpaEntity e = new GenderJpaEntity();
        e.setId(id); e.setName(name);
        return e;
    }

    @Test
    void findById_found_returnsGender() {
        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity(1, "Male")));
        Optional<Gender> result = adapter.findById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().id());
        assertEquals("Male", result.get().name());
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(jpaRepository.findById(99)).thenReturn(Optional.empty());
        assertTrue(adapter.findById(99).isEmpty());
    }

    @Test
    void findAll_returnsMappedList() {
        when(jpaRepository.findAll()).thenReturn(List.of(entity(1,"Male"), entity(2,"Female")));
        List<Gender> result = adapter.findAll();
        assertEquals(2, result.size());
        assertEquals("Male", result.get(0).name());
    }

    @Test
    void findAll_emptyRepository_returnsEmptyList() {
        when(jpaRepository.findAll()).thenReturn(List.of());
        assertTrue(adapter.findAll().isEmpty());
    }
}
