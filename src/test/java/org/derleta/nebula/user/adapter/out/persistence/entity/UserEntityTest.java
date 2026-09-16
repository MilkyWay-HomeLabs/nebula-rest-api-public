package org.derleta.nebula.user.adapter.out.persistence.entity;

import org.junit.jupiter.api.Test;
import java.sql.Date;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void updateAge_calculateCorrectAge() {
        UserEntity entity = new UserEntity();
        entity.setBirthDate(Date.valueOf(LocalDate.now().minusYears(25)));
        entity.updateAge();
        assertEquals(25, entity.getAge());
    }

    @Test
    void updateAge_addsAgeField() {
        UserEntity entity = new UserEntity();
        entity.setBirthDate(Date.valueOf(LocalDate.of(1990, 1, 1)));
        entity.updateAge();
        assertNotNull(entity.getAge());
        assertTrue(entity.getAge() > 0);
    }
}
