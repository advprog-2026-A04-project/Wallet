package id.ac.ui.cs.a04.json.wallet.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InternalTokenAuthenticationFilterTest {

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateMatchingInternalToken() throws Exception {
        InternalTokenAuthenticationFilter filter = new InternalTokenAuthenticationFilter("shared-token");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Internal-Token", "shared-token");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertEquals("internal-service", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void shouldIgnoreMismatchedInternalToken() throws Exception {
        InternalTokenAuthenticationFilter filter = new InternalTokenAuthenticationFilter("shared-token");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Internal-Token", "different");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldIgnoreMissingInternalToken() throws Exception {
        InternalTokenAuthenticationFilter filter = new InternalTokenAuthenticationFilter("shared-token");

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldSkipWhenAuthenticationAlreadyExists() throws Exception {
        InternalTokenAuthenticationFilter filter = new InternalTokenAuthenticationFilter("shared-token");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("7", null));

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());

        assertEquals("7", SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
