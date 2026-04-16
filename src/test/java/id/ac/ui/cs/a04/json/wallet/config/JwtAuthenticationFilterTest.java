package id.ac.ui.cs.a04.json.wallet.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldPassThroughWithoutBearerToken() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(mock(JwtService.class));

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldPassThroughWithNonBearerToken() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(mock(JwtService.class));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc123");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldAuthenticateValidToken() throws Exception {
        String secret = "json-milestone-secret-json-milestone-secret";
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(new JwtService(secret));
        String token = Jwts.builder()
                .subject("5")
                .claims(Map.of("role", "TITIPER"))
                .issuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/wallet/balance");
        request.addHeader("Authorization", "Bearer " + token);

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("5", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void shouldSkipHealthEndpoint() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(mock(JwtService.class));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/actuator/health");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldSkipWhenAuthenticationAlreadyExists() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(mock(JwtService.class));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("5", null));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/wallet/balance");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertEquals("5", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void shouldReturnUnauthorizedForInvalidToken() throws Exception {
        JwtService jwtService = mock(JwtService.class);
        when(jwtService.parseToken("broken")).thenThrow(new JwtException("broken"));
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/wallet/balance");
        request.addHeader("Authorization", "Bearer broken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
    }
}
