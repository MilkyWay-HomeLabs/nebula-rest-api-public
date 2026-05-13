package org.derleta.nebula.gender.adapter.out.persistence.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.derleta.nebula.gender.adapter.out.persistence.entity.GenderJpaEntity;
import org.derleta.nebula.gender.domain.model.Gender;

/** Maps between Gender domain model and GenderJpaEntity. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenderPersistenceMapper {

    public static Gender toDomain(GenderJpaEntity entity) {
        return new Gender(entity.getId(), entity.getName());
    }

    public static GenderJpaEntity toEntity(Gender gender) {
        return new GenderJpaEntity(gender.id(), gender.name());
    }
}

