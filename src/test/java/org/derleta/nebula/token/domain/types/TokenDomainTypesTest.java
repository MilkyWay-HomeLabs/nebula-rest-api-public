package org.derleta.nebula.token.domain.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TokenDomainTypesTest {

    @Test
    void tokenResponseType_allValues_haveIdAppCodeInfo() {
        for (TokenResponseType t : TokenResponseType.values()) {
            assertNotNull(t.name());
            assertNotNull(t.getAppCode());
            assertNotNull(t.getInfo());
            assertTrue(t.getId() > 0);
        }
    }

    @Test
    void tokenResponseType_toString_notBlank() {
        for (TokenResponseType t : TokenResponseType.values()) {
            assertFalse(t.toString().isBlank());
        }
    }

    @Test
    void accessProcessType_allValues_haveIdAndName() {
        AccessProcessType[] values = AccessProcessType.values();
        assertTrue(values.length > 0);
        for (AccessProcessType a : values) {
            assertTrue(a.getId() > 0);
            assertNotNull(a.getName());
            assertFalse(a.toString().isBlank());
        }
    }

    @Test
    void accessResponseType_allValues_nameAndToString() {
        AccessResponseType[] values = AccessResponseType.values();
        assertTrue(values.length > 0);
        for (AccessResponseType a : values) {
            assertNotNull(a.name());
            assertFalse(a.toString().isBlank());
        }
    }

    @Test
    void accessResponseType_nonNull_haveAppCode() {
        assertNotNull(AccessResponseType.ACCESS_REFRESHED.toString());
        assertNotNull(AccessResponseType.ACCESS_NOT_REFRESHED.toString());
    }
}
