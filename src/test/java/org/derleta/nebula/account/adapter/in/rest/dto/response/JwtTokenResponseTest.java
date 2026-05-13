package org.derleta.nebula.account.adapter.in.rest.dto.response;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenResponseTest {

    @Test
    void noArgsConstructor_andSetters_work() {
        JwtTokenResponse resp = new JwtTokenResponse();
        resp.setCookiesHeaders(Map.of("Authorization", "Bearer abc"));
        assertEquals("Bearer abc", resp.getCookiesHeaders().get("Authorization"));
    }

    @Test
    void allArgsConstructor_getters_work() {
        Map<String, String> cookies = Map.of("k", "v");
        JwtTokenResponse resp = new JwtTokenResponse(cookies, "user1", "u@t.com");
        assertEquals("user1", resp.getUsername());
        assertEquals("u@t.com", resp.getEmail());
        assertEquals(cookies, resp.getCookiesHeaders());
    }
}

