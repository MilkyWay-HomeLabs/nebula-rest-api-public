package org.derleta.nebula.theme.adapter.out.persistence;

import org.derleta.nebula.theme.adapter.out.persistence.entity.ThemeEntity;
import org.derleta.nebula.theme.adapter.out.persistence.jpa.ThemeJpaRepository;
import org.derleta.nebula.theme.domain.model.Theme;
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
class ThemeJpaAdapterTest {

    @Mock private ThemeJpaRepository repository;
    @InjectMocks private ThemeJpaAdapter adapter;

    private ThemeEntity entity(int id, String name) {
        return new ThemeEntity(id, name);
    }

    @Test
    void findById_found_returnsTheme() {
        when(repository.findById(1)).thenReturn(Optional.of(entity(1, "Default")));
        Optional<Theme> result = adapter.findById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().id());
        assertEquals("Default", result.get().name());
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(repository.findById(99)).thenReturn(Optional.empty());
        assertTrue(adapter.findById(99).isEmpty());
    }

    @Test
    void findAll_returnsMappedList() {
        when(repository.findAll()).thenReturn(List.of(entity(1,"Default"), entity(2,"Dark")));
        List<Theme> result = adapter.findAll();
        assertEquals(2, result.size());
        assertEquals("Dark", result.get(1).name());
    }

    @Test
    void findAll_empty_returnsEmptyList() {
        when(repository.findAll()).thenReturn(List.of());
        assertTrue(adapter.findAll().isEmpty());
    }
}
