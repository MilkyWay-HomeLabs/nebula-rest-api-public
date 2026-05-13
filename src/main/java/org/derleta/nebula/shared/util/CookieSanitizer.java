package org.derleta.nebula.shared.util;

/** Utility class for sanitizing cookie values to prevent CRLF injection and other header-related attacks. */
public final class CookieSanitizer {

    private CookieSanitizer() {
        // Utility class
    }

    /**
     * Sanitizes a cookie value by removing CRLF characters and other potentially dangerous characters.
     * Returns an empty string if the input is null or empty.
     *
     * @param cookieValue the raw cookie value
     * @return the sanitized cookie value
     */
    public static String sanitize(String cookieValue) {
        if (cookieValue == null || cookieValue.isEmpty()) {
            return "";
        }
        return cookieValue
                .replaceAll("[\\r\\n]", "")
                .replaceAll("[^a-zA-Z0-9=;\\s\\-_./+]", "");
    }
}
