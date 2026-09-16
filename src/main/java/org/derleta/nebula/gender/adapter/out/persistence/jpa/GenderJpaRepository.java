package org.derleta.nebula.gender.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.derleta.nebula.gender.adapter.out.persistence.entity.GenderJpaEntity;

@Repository
public interface GenderJpaRepository extends JpaRepository<GenderJpaEntity, Integer> {}

