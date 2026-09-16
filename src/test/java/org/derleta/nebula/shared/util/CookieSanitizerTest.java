package org.derleta.nebula.shared.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CookieSanitizerTest {

    @Test
    void sanitize_normalValue_remainsUnchanged() {
        String input = "accessToken=xyz123-ABC_456.789; Path=/; HttpOnly";
        assertEquals(input, CookieSanitizer.sanitize(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void sanitize_nullOrEmpty_returnsEmptyString(String input) {
        assertEquals("", CookieSanitizer.sanitize(input));
    }

    @Test
    void sanitize_crlfInjection_removesCrlf() {
        String input = "val\r\nInjected-Header: evil";
        String expected = "valInjected-Header evil"; // ':' is also removed by whitelist
        assertEquals(expected, CookieSanitizer.sanitize(input));
    }

    @ParameterizedTest
    @CsvSource({
            "'val\r', 'val'",
            "'val\n', 'val'",
            "'val\r\n', 'val'",
            "'v\ra\nl', 'val'"
    })
    void sanitize_crAndLf_removesThem(String input, String expected) {
        assertEquals(expected, CookieSanitizer.sanitize(input));
    }

    @Test
    void sanitize_dangerousCharacters_removesThem() {
        // Allowed: a-zA-Z0-9=;\s\-_./
        String input = "val<script>alert(1)</script>:{}[]|\\^\"'`";
        String expected = "valscriptalert1/script"; 
        assertEquals(expected, CookieSanitizer.sanitize(input));
    }

    @Test
    void sanitize_allowedSpecialCharacters_preservesThem() {
        String input = "accessToken=val-123_abc.XYZ; Path=/; HttpOnly";
        assertEquals(input, CookieSanitizer.sanitize(input));
    }
}
