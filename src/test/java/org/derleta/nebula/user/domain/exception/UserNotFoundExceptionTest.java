package org.derleta.nebula.user.domain.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserNotFoundExceptionTest {

    @Test
    void constructor_storesMessage() {
        UserNotFoundException ex = new UserNotFoundException("User 42 not found");
        assertEquals("User 42 not found", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }
}
