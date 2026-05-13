package org.derleta.nebula.account.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.derleta.nebula.user.adapter.out.persistence.entity.UserEntity;

/** JPA repository for UserEntity — scoped to the account bounded context. */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {}

