package org.derleta.nebula.user.adapter.out.persistence.entity.id;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsersGameIdTest {

    @Test
    void equalsAndHashCode_sameValues_areEqual() {
        UsersGameId id1 = new UsersGameId();
        id1.setUserId(1L); id1.setGameId(2);

        UsersGameId id2 = new UsersGameId();
        id2.setUserId(1L); id2.setGameId(2);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void equals_sameInstance_isEqual() {
        UsersGameId id = new UsersGameId();
        id.setUserId(1L); id.setGameId(2);
        assertEquals(id, id);
    }

    @Test
    void equals_null_isFalse() {
        UsersGameId id = new UsersGameId();
        id.setUserId(1L); id.setGameId(2);
        assertNotEquals(id, null);
    }

    @Test
    void equals_differentValues_notEqual() {
        UsersGameId id1 = new UsersGameId();
        id1.setUserId(1L); id1.setGameId(2);

        UsersGameId id2 = new UsersGameId();
        id2.setUserId(1L); id2.setGameId(3);

        assertNotEquals(id1, id2);
    }

    @Test
    void gettersSetters_work() {
        UsersGameId id = new UsersGameId();
        id.setUserId(5L);
        id.setGameId(10);
        assertEquals(5L, id.getUserId());
        assertEquals(10, id.getGameId());
    }

    @Test
    void equals_differentClass_isFalse() {
        UsersGameId id = new UsersGameId();
        id.setUserId(1L); id.setGameId(2);
        assertNotEquals(id, "string");
        assertNotEquals(id, new Object());
    }
}
