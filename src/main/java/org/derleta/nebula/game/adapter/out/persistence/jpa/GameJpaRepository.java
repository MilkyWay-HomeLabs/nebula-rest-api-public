package org.derleta.nebula.game.adapter.out.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.derleta.nebula.game.adapter.out.persistence.entity.GameEntity;

import java.util.List;
import java.util.Optional;

/** JPA repository for the games table. */
@Repository
public interface GameJpaRepository extends JpaRepository<GameEntity, Integer> {

    @Query("SELECT MAX(ge.id) + 1 FROM GameEntity ge")
    int getNextId();

    Page<GameEntity> findAll(Specification<GameEntity> spec, Pageable pageable);

    @Query("SELECT g FROM GameEntity g WHERE g.name = :name")
    Optional<GameEntity> findByName(@Param("name") String name);

    @Query("SELECT g FROM GameEntity g WHERE g.name = :name AND g.id <> :selfId")
    Optional<GameEntity> findByNameOtherThanSelfId(@Param("selfId") Integer selfId, @Param("name") String name);

    @Query("SELECT g FROM GameEntity g WHERE g.enable = true")
    List<GameEntity> getEnabled();
}

