package org.derleta.nebula.gender.adapter.out.persistence.mapper;

import org.derleta.nebula.gender.adapter.out.persistence.entity.GenderJpaEntity;
import org.derleta.nebula.gender.domain.model.Gender;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GenderPersistenceMapperTest {

    @Test
    void toDomain_mapsEntityToGender() {
        GenderJpaEntity e = new GenderJpaEntity();
        e.setId(1); e.setName("Male");
        Gender g = GenderPersistenceMapper.toDomain(e);
        assertEquals(1, g.id());
        assertEquals("Male", g.name());
    }

    @Test
    void toEntity_mapsGenderToEntity() {
        Gender g = new Gender(2, "Female");
        GenderJpaEntity e = GenderPersistenceMapper.toEntity(g);
        assertEquals(2, e.getId());
        assertEquals("Female", e.getName());
    }
}
