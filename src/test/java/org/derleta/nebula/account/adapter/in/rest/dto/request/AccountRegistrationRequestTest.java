package org.derleta.nebula.account.adapter.in.rest.dto.request;

import org.junit.jupiter.api.Test;
import java.sql.Date;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class AccountRegistrationRequestTest {

    @Test
    void builder_createsObjectWithAllFields() {
        Date bd = Date.valueOf(LocalDate.of(1990,1,1));
        AccountRegistrationRequest req = AccountRegistrationRequest.builder()
                .login("testuser")
                .email("test@example.com")
                .password("secret123")
                .birthdate(bd)
                .nationality(5)
                .gender(1)
                .build();
        assertEquals("testuser", req.getLogin());
        assertEquals("test@example.com", req.getEmail());
        assertEquals("secret123", req.getPassword());
        assertEquals(bd, req.getBirthdate());
        assertEquals(5, req.getNationality());
        assertEquals(1, req.getGender());
    }

    @Test
    void toString_containsLoginAndEmail() {
        AccountRegistrationRequest req = AccountRegistrationRequest.builder()
                .login("john").email("j@x.com").password("p123456").build();
        String s = req.toString();
        assertTrue(s.contains("john"));
        assertTrue(s.contains("j@x.com"));
    }
}
