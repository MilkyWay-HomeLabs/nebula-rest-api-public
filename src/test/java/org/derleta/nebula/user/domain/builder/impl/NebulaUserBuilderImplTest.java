package org.derleta.nebula.user.domain.builder.impl;

import org.derleta.nebula.gender.domain.model.Gender;
import org.derleta.nebula.nationality.domain.model.Nationality;
import org.derleta.nebula.nationality.domain.model.Region;
import org.derleta.nebula.user.domain.model.NebulaUser;
import org.derleta.nebula.user.domain.model.UserSettings;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NebulaUserBuilderImplTest {

    @Test
    void build_allFields_createsCorrectNebulaUser() {
        Date bd = Date.valueOf(LocalDate.of(1993, 5, 15));
        Gender gender = new Gender(1, "Male");
        Region region = new Region(1, "Europe");
        Nationality nationality = new Nationality(1, "Polish", "POL", region);
        UserSettings settings = new UserSettings(1L, null, null);

        NebulaUser user = new NebulaUserBuilderImpl()
                .id(1L)
                .login("jdoe")
                .email("j@example.com")
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .birthDate(bd)
                .gender(gender)
                .nationality(nationality)
                .settings(settings)
                .games(Collections.emptyList())
                .achievements(Collections.emptyList())
                .build();

        assertEquals(1L, user.id());
        assertEquals("jdoe", user.login());
        assertEquals("j@example.com", user.email());
        assertEquals("John", user.firstName());
        assertEquals("Doe", user.lastName());
        assertEquals(30, user.age());
        assertEquals(bd, user.birthDate());
        assertEquals(gender, user.gender());
        assertEquals(nationality, user.nationality());
        assertEquals(settings, user.settings());
        assertTrue(user.games().isEmpty());
        assertTrue(user.achievements().isEmpty());
    }

    @Test
    void build_withNullOptionalFields_doesNotThrow() {
        NebulaUser user = new NebulaUserBuilderImpl()
                .id(2L)
                .login("test")
                .email("test@test.com")
                .firstName(null)
                .lastName(null)
                .age(0)
                .birthDate(null)
                .gender(null)
                .nationality(null)
                .settings(null)
                .games(List.of())
                .achievements(List.of())
                .build();

        assertNotNull(user);
        assertNull(user.firstName());
        assertNull(user.lastName());
    }

    @Test
    void builder_eachSetterReturnsSameBuilderInstance() {
        NebulaUserBuilderImpl builder = new NebulaUserBuilderImpl();
        assertSame(builder, builder.id(1L));
        assertSame(builder, builder.login("x"));
        assertSame(builder, builder.email("x@x.com"));
        assertSame(builder, builder.firstName("F"));
        assertSame(builder, builder.lastName("L"));
        assertSame(builder, builder.age(1));
        assertSame(builder, builder.birthDate(null));
        assertSame(builder, builder.gender(null));
        assertSame(builder, builder.nationality(null));
        assertSame(builder, builder.settings(null));
        assertSame(builder, builder.games(List.of()));
        assertSame(builder, builder.achievements(List.of()));
    }
}

