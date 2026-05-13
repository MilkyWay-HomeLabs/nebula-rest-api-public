package org.derleta.nebula.userachievement.adapter.out.persistence.entity.id;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompositeIdsTest {

    // --- UserAchievementId ---
    @Test
    void userAchievementId_equalsSameValues() {
        UserAchievementId a = new UserAchievementId();
        a.setUserId(1L); a.setAchievementId(2);
        UserAchievementId b = new UserAchievementId();
        b.setUserId(1L); b.setAchievementId(2);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void userAchievementId_sameInstance_isEqual() {
        UserAchievementId a = new UserAchievementId();
        a.setUserId(1L); a.setAchievementId(2);
        assertEquals(a, a);
    }

    @Test
    void userAchievementId_null_notEqual() {
        UserAchievementId a = new UserAchievementId();
        a.setUserId(1L); a.setAchievementId(2);
        assertNotEquals(null, a);
    }

    @Test
    void userAchievementId_differentValues_notEqual() {
        UserAchievementId a = new UserAchievementId();
        a.setUserId(1L); a.setAchievementId(2);
        UserAchievementId b = new UserAchievementId();
        b.setUserId(1L); b.setAchievementId(3);
        assertNotEquals(a, b);
    }

    @Test
    void userAchievementId_gettersSetters_work() {
        UserAchievementId id = new UserAchievementId();
        id.setUserId(7L); id.setAchievementId(9);
        assertEquals(7L, id.getUserId());
        assertEquals(9, id.getAchievementId());
    }

    @Test
    void userAchievementId_differentClass_notEqual() {
        UserAchievementId a = new UserAchievementId();
        a.setUserId(1L); a.setAchievementId(2);
        assertNotEquals("some string", a);
        assertNotEquals(new Object(), a);
    }

    // --- AchievementLevelId ---
    @Test
    void achievementLevelId_equalsSameValues() {
        AchievementLevelId a = new AchievementLevelId();
        a.setAchievementId(3); a.setLevel(2);
        AchievementLevelId b = new AchievementLevelId();
        b.setAchievementId(3); b.setLevel(2);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void achievementLevelId_sameInstance_isEqual() {
        AchievementLevelId a = new AchievementLevelId();
        a.setAchievementId(3);
        a.setLevel(2);
        assertEquals(a, a);
    }

    @Test
    void achievementLevelId_null_notEqual() {
        AchievementLevelId a = new AchievementLevelId();
        a.setAchievementId(3);
        a.setLevel(2);
        assertNotEquals(a, null);
    }

    @Test
    void achievementLevelId_differentValues_notEqual() {
        AchievementLevelId a = new AchievementLevelId();
        a.setAchievementId(3); a.setLevel(2);
        AchievementLevelId b = new AchievementLevelId();
        b.setAchievementId(3); b.setLevel(5);
        assertNotEquals(a, b);
    }

    @Test
    void achievementLevelId_gettersSetters_work() {
        AchievementLevelId id = new AchievementLevelId();
        id.setAchievementId(11); id.setLevel(3);
        assertEquals(11, id.getAchievementId());
        assertEquals(3, id.getLevel());
    }

    @Test
    void achievementLevelId_differentClass_notEqual() {
        AchievementLevelId a = new AchievementLevelId();
        a.setAchievementId(3); a.setLevel(2);
        assertNotEquals("some string", a);
        assertNotEquals(new Object(), a);
    }
}
