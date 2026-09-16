package org.derleta.nebula.game.adapter.out.persistence;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.derleta.nebula.game.adapter.out.persistence.entity.GameEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class GameSpecificationsTest {

    @SuppressWarnings("unchecked")
    @Test
    void hasAllFilters_ValidNameAndEnable_ReturnsCombinedPredicate() {
        String name = "test";
        boolean enable = true;
        Specification<GameEntity> specification = GameSpecifications.hasAllFilters(name, enable);

        Root<GameEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate namePredicate = mock(Predicate.class);
        Predicate enablePredicate = mock(Predicate.class);
        Predicate combinedPredicate = mock(Predicate.class);

        when(cb.like(root.get("name"), "%test%")).thenReturn(namePredicate);
        when(cb.equal(root.get("enable"), true)).thenReturn(enablePredicate);
        when(cb.and(namePredicate, enablePredicate)).thenReturn(combinedPredicate);

        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(combinedPredicate, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    void hasAllFilters_NullName_ReturnsCombinedPredicateWithWildcard() {
        Specification<GameEntity> specification = GameSpecifications.hasAllFilters(null, true);

        Root<GameEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate namePredicate = mock(Predicate.class);
        Predicate enablePredicate = mock(Predicate.class);
        Predicate combinedPredicate = mock(Predicate.class);

        when(cb.like(root.get("name"), "%%")).thenReturn(namePredicate);
        when(cb.equal(root.get("enable"), true)).thenReturn(enablePredicate);
        when(cb.and(namePredicate, enablePredicate)).thenReturn(combinedPredicate);

        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(combinedPredicate, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    void hasAllFilters_EmptyName_ReturnsCombinedPredicateWithWildcard() {
        Specification<GameEntity> specification = GameSpecifications.hasAllFilters("", true);

        Root<GameEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate namePredicate = mock(Predicate.class);
        Predicate enablePredicate = mock(Predicate.class);
        Predicate combinedPredicate = mock(Predicate.class);

        when(cb.like(root.get("name"), "%%")).thenReturn(namePredicate);
        when(cb.equal(root.get("enable"), true)).thenReturn(enablePredicate);
        when(cb.and(namePredicate, enablePredicate)).thenReturn(combinedPredicate);

        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(combinedPredicate, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    void hasAllFilters_EnableFalse_ReturnsCombinedPredicateWithDisabled() {
        Specification<GameEntity> specification = GameSpecifications.hasAllFilters("test", false);

        Root<GameEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate namePredicate = mock(Predicate.class);
        Predicate enablePredicate = mock(Predicate.class);
        Predicate combinedPredicate = mock(Predicate.class);

        when(cb.like(root.get("name"), "%test%")).thenReturn(namePredicate);
        when(cb.equal(root.get("enable"), false)).thenReturn(enablePredicate);
        when(cb.and(namePredicate, enablePredicate)).thenReturn(combinedPredicate);

        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(combinedPredicate, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    void hasName_ValidName_ReturnsExpectedPredicate() {
        Specification<GameEntity> specification = GameSpecifications.hasName("test");

        Root<GameEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate namePredicate = mock(Predicate.class);
        when(cb.like(root.get("name"), "%test%")).thenReturn(namePredicate);

        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(namePredicate, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    void hasName_NullName_ReturnsWildcardPredicate() {
        Specification<GameEntity> specification = GameSpecifications.hasName(null);

        Root<GameEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate namePredicate = mock(Predicate.class);
        when(cb.like(root.get("name"), "%%")).thenReturn(namePredicate);

        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(namePredicate, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    void hasName_EmptyName_ReturnsWildcardPredicate() {
        Specification<GameEntity> specification = GameSpecifications.hasName("");

        Root<GameEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate namePredicate = mock(Predicate.class);
        when(cb.like(root.get("name"), "%%")).thenReturn(namePredicate);

        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(namePredicate, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    void isEnable_EnableTrue_ReturnsExpectedPredicate() {
        Specification<GameEntity> specification = GameSpecifications.isEnable(true);

        Root<GameEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate enablePredicate = mock(Predicate.class);
        when(cb.equal(root.get("enable"), true)).thenReturn(enablePredicate);

        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(enablePredicate, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    void isEnable_EnableFalse_ReturnsExpectedPredicate() {
        Specification<GameEntity> specification = GameSpecifications.isEnable(false);

        Root<GameEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate enablePredicate = mock(Predicate.class);
        when(cb.equal(root.get("enable"), false)).thenReturn(enablePredicate);

        Predicate result = specification.toPredicate(root, query, cb);

        assertNotNull(result);
        assertEquals(enablePredicate, result);
    }
}

