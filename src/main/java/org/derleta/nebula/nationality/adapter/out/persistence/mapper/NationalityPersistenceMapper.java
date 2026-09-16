package org.derleta.nebula.nationality.adapter.out.persistence.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.nationality.adapter.out.persistence.entity.NationalityJpaEntity;
import org.derleta.nebula.nationality.domain.model.Nationality;
import org.derleta.nebula.nationality.domain.model.Region;

/** Maps between Nationality/Region domain models and their JPA entities. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NationalityPersistenceMapper {

    public static Nationality toDomain(NationalityJpaEntity entity) {
        Region region = new Region(entity.getRegion().getId(), entity.getRegion().getName());
        return new Nationality(entity.getId(), entity.getName(), entity.getCode(), region);
    }
}

