package org.derleta.nebula.user.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserSettingsEntity;

@Repository
public interface UserSettingsJpaRepository extends JpaRepository<UserSettingsEntity, Long> {
}

