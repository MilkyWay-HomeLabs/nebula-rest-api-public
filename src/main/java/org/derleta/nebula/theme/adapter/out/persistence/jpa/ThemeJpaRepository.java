package org.derleta.nebula.theme.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.derleta.nebula.theme.adapter.out.persistence.entity.ThemeEntity;

/** JPA repository for ThemeEntity — scoped to the theme bounded context. */
@Repository
public interface ThemeJpaRepository extends JpaRepository<ThemeEntity, Integer> {}

