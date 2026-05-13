package org.derleta.nebula.userachievement.adapter.out.persistence;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;
import org.derleta.nebula.userachievement.adapter.out.persistence.entity.UserAchievementEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"unchecked"})
class UserAchievementSpecificationsTest {

    @Test
    void hasNotEqualLevel_shouldCreateCorrectPredicate() {
        int level = 3;
        Root<UserAchievementEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        var levelPath = Mockito.mock(jakarta.persistence.criteria.Path.class);
        when(root.get("level")).thenReturn(levelPath);
        Predicate expected = mock(Predicate.class);
        when(cb.notEqual(levelPath, level)).thenReturn(expected);

        Specification<UserAchievementEntity> spec = UserAchievementSpecifications.hasNotEqualLevel(level);
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(expected, result);
        verify(root).get("level");
        verify(cb).notEqual(levelPath, level);
    }

    @Test
    void hasEqualLevel_shouldCreateCorrectPredicate() {
        int level = 3;
        Root<UserAchievementEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        var levelPath = Mockito.mock(jakarta.persistence.criteria.Path.class);
        when(root.get("level")).thenReturn(levelPath);
        Predicate expected = mock(Predicate.class);
        when(cb.equal(levelPath, level)).thenReturn(expected);

        Specification<UserAchievementEntity> spec = UserAchievementSpecifications.hasEqualLevel(level);
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(expected, result);
        verify(root).get("level");
        verify(cb).equal(levelPath, level);
    }

    @Test
    void hasGreaterLevel_shouldCreateCorrectPredicate() {
        int level = 3;
        Root<UserAchievementEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        var levelPath = Mockito.mock(jakarta.persistence.criteria.Path.class);
        when(root.get("level")).thenReturn(levelPath);
        Predicate expected = mock(Predicate.class);
        when(cb.greaterThan(levelPath, level)).thenReturn(expected);

        Specification<UserAchievementEntity> spec = UserAchievementSpecifications.hasGreaterLevel(level);
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(expected, result);
        verify(root).get("level");
        verify(cb).greaterThan(levelPath, level);
    }

    @Test
    void hasGreaterOrEqualLevel_shouldCreateCorrectPredicate() {
        int level = 3;
        Root<UserAchievementEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        var levelPath = Mockito.mock(jakarta.persistence.criteria.Path.class);
        when(root.get("level")).thenReturn(levelPath);
        Predicate expected = mock(Predicate.class);
        when(cb.greaterThanOrEqualTo(levelPath, level)).thenReturn(expected);

        Specification<UserAchievementEntity> spec = UserAchievementSpecifications.hasGreaterOrEqualLevel(level);
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(expected, result);
        verify(root).get("level");
        verify(cb).greaterThanOrEqualTo(levelPath, level);
    }

    @Test
    void hasLessLevel_shouldCreateCorrectPredicate() {
        int level = 3;
        Root<UserAchievementEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        var levelPath = Mockito.mock(jakarta.persistence.criteria.Path.class);
        when(root.get("level")).thenReturn(levelPath);
        Predicate expected = mock(Predicate.class);
        when(cb.lessThan(levelPath, level)).thenReturn(expected);

        Specification<UserAchievementEntity> spec = UserAchievementSpecifications.hasLessLevel(level);
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(expected, result);
        verify(root).get("level");
        verify(cb).lessThan(levelPath, level);
    }

    @Test
    void hasLessOrEqualLevel_shouldCreateCorrectPredicate() {
        int level = 3;
        Root<UserAchievementEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        var levelPath = Mockito.mock(jakarta.persistence.criteria.Path.class);
        when(root.get("level")).thenReturn(levelPath);
        Predicate expected = mock(Predicate.class);
        when(cb.lessThanOrEqualTo(levelPath, level)).thenReturn(expected);

        Specification<UserAchievementEntity> spec = UserAchievementSpecifications.hasLessOrEqualLevel(level);
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(expected, result);
        verify(root).get("level");
        verify(cb).lessThanOrEqualTo(levelPath, level);
    }

    @ParameterizedTest
    @CsvSource({
            "greater or equal, greaterThanOrEqualTo",
            "less or equal, lessThanOrEqualTo",
            "greater, greaterThan",
            "less, lessThan",
            "notequal, notEqual",
            "equal, equal",
            ", equal"
    })
    void hasAllFilters_shouldReturnCorrectSpecification(String filterType, String expectedMethod) {
        int level = 3;
        Root<UserAchievementEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        var levelPath = Mockito.mock(jakarta.persistence.criteria.Path.class);
        when(root.get("level")).thenReturn(levelPath);

        Predicate equalPred = mock(Predicate.class);
        Predicate notEqualPred = mock(Predicate.class);
        Predicate greaterPred = mock(Predicate.class);
        Predicate greaterOrEqualPred = mock(Predicate.class);
        Predicate lessPred = mock(Predicate.class);
        Predicate lessOrEqualPred = mock(Predicate.class);

        when(cb.equal(levelPath, level)).thenReturn(equalPred);
        when(cb.notEqual(levelPath, level)).thenReturn(notEqualPred);
        when(cb.greaterThan(levelPath, level)).thenReturn(greaterPred);
        when(cb.greaterThanOrEqualTo(levelPath, level)).thenReturn(greaterOrEqualPred);
        when(cb.lessThan(levelPath, level)).thenReturn(lessPred);
        when(cb.lessThanOrEqualTo(levelPath, level)).thenReturn(lessOrEqualPred);

        Specification<UserAchievementEntity> spec =
                UserAchievementSpecifications.hasAllFilters(level, filterType);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(root, atLeastOnce()).get("level");

        switch (expectedMethod) {
            case "equal"                -> verify(cb).equal(levelPath, level);
            case "notEqual"             -> verify(cb).notEqual(levelPath, level);
            case "greaterThan"          -> verify(cb).greaterThan(levelPath, level);
            case "greaterThanOrEqualTo" -> verify(cb).greaterThanOrEqualTo(levelPath, level);
            case "lessThan"             -> verify(cb).lessThan(levelPath, level);
            case "lessThanOrEqualTo"    -> verify(cb).lessThanOrEqualTo(levelPath, level);
        }
    }

    @Test
    void hasAllFilters_nullFilterType_defaultsToEqual() {
        int level = 3;
        Root<UserAchievementEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        var levelPath = Mockito.mock(jakarta.persistence.criteria.Path.class);
        when(root.get("level")).thenReturn(levelPath);
        Predicate expected = mock(Predicate.class);
        when(cb.equal(levelPath, level)).thenReturn(expected);

        Specification<UserAchievementEntity> spec =
                UserAchievementSpecifications.hasAllFilters(level, null);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(root).get("level");
        verify(cb).equal(levelPath, level);
    }

    @Test
    void hasAllFilters_unknownFilterType_defaultsToEqual() {
        int level = 3;
        Root<UserAchievementEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        var levelPath = Mockito.mock(jakarta.persistence.criteria.Path.class);
        when(root.get("level")).thenReturn(levelPath);
        Predicate expected = mock(Predicate.class);
        when(cb.equal(levelPath, level)).thenReturn(expected);

        Specification<UserAchievementEntity> spec =
                UserAchievementSpecifications.hasAllFilters(level, "unknown");
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(root).get("level");
        verify(cb).equal(levelPath, level);
    }
}
