package org.derleta.nebula.nationality.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.derleta.nebula.nationality.adapter.out.persistence.entity.NationalityJpaEntity;

@Repository
public interface NationalityJpaRepository extends JpaRepository<NationalityJpaEntity, Integer> {}

