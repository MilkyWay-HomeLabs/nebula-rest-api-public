package org.derleta.nebula.nationality.adapter.out.persistence.mapper;

import org.derleta.nebula.nationality.adapter.out.persistence.entity.NationalityJpaEntity;
import org.derleta.nebula.nationality.adapter.out.persistence.entity.RegionJpaEntity;
import org.derleta.nebula.nationality.domain.model.Nationality;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NationalityPersistenceMapperTest {

    @Test
    void toDomain_mapsEntityToNationality() {
        RegionJpaEntity regionEntity = new RegionJpaEntity(3, "Asia");
        NationalityJpaEntity entity = new NationalityJpaEntity(10, "Japanese", "JPN", regionEntity);

        Nationality result = NationalityPersistenceMapper.toDomain(entity);

        assertEquals(10, result.id());
        assertEquals("Japanese", result.name());
        assertEquals("JPN", result.code());
        assertNotNull(result.region());
        assertEquals(3, result.region().id());
        assertEquals("Asia", result.region().name());
    }
}
