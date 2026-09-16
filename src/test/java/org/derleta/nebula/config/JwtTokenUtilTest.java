package org.derleta.nebula.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.derleta.nebula.shared.security.JwtTokenUtil;
import org.derleta.nebula.shared.security.model.Role;
import org.derleta.nebula.shared.security.model.TokenData;
import org.derleta.nebula.shared.domain.exception.TokenExpiredException;

import javax.crypto.SecretKey;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenUtilTest {

    @Spy
    @InjectMocks
    private JwtTokenUtil jwtTokenUtil;

    private String validToken;
    private String validTokenWithoutEmailInSubject;
    private final String SECRET_KEY = "dGhpc0lzQVRlc3RTZWNyZXRLZXlUaGF0SXNMb25nRW5vdWdoRm9ySG1hY1NoYTI1NlNpZ25hdHVyZQ==";
    private final Long userId = 1L;
    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenUtil, "SECRET_KEY", SECRET_KEY);

        // Create a valid token for testing
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

        Map<String, Object> role1 = new HashMap<>();
        role1.put("id", 1);
        role1.put("name", "USER");

        Map<String, Object> role2 = new HashMap<>();
        role2.put("id", 2);
        role2.put("name", "ADMIN");

        List<Map<String, Object>> roles = Arrays.asList(role1, role2);

        // Create a token that expires in 1 hour
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);

        validToken = Jwts.builder()
                .subject(userId + "," + email)
                .claim("roles", roles)
                .expiration(calendar.getTime())
                .signWith(key)
                .compact();

        validTokenWithoutEmailInSubject = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("roles", roles)
                .expiration(calendar.getTime())
                .signWith(key)
                .compact();
    }

    @Test
    void getTokenData_validToken_returnsTokenData() {
        // Act
        TokenData tokenData = jwtTokenUtil.getTokenData(validToken);

        // Assert
        assertNotNull(tokenData);
        assertTrue(tokenData.isValid());
        assertEquals(email, tokenData.getEmail());
        assertEquals(userId, tokenData.getUserId());
        assertEquals(validToken, tokenData.getToken());
    }

    @Test
    void getTokenData_expiredToken_returnsNull() {
        doReturn(true).when(jwtTokenUtil).isTokenExpired(anyString());

        // Act & Assert
        assertThrows(TokenExpiredException.class, () -> jwtTokenUtil.getTokenData("expired-token"));
    }

    @Test
    void isTokenExpired_validToken_returnsFalse() {
        // Act
        boolean isExpired = jwtTokenUtil.isTokenExpired(validToken);

        // Assert
        assertFalse(isExpired);
    }

    @Test
    void getRoles_validToken_returnsRoles() {
        // Act
        Set<Role> roles = jwtTokenUtil.getRoles(validToken);

        // Assert
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.stream().anyMatch(role -> role.getRoleId() == 1 && "USER".equals(role.getRoleName())));
        assertTrue(roles.stream().anyMatch(role -> role.getRoleId() == 2 && "ADMIN".equals(role.getRoleName())));
    }

    @Test
    void getEmail_validToken_returnsEmail() {
        // Act
        String extractedEmail = jwtTokenUtil.getEmail(validToken);

        // Assert
        assertEquals(email, extractedEmail);
    }

    @Test
    void getEmail_tokenWithoutEmailInSubject_returnsEmptyString() {
        String extractedEmail = jwtTokenUtil.getEmail(validTokenWithoutEmailInSubject);

        assertEquals("", extractedEmail);
    }

    @Test
    void getUserId_validToken_returnsUserId() {
        // Act
        Long extractedUserId = jwtTokenUtil.getUserId(validToken);

        // Assert
        assertEquals(userId, extractedUserId);
    }

}
