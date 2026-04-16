package id.ac.ui.cs.a04.json.wallet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletAccessGuardTest {

    private final WalletAccessGuard guard = new WalletAccessGuard();

    @Test
    void shouldRejectMissingAuthentication() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> guard.requireUserAccess(null, 1L, true)
        );

        assertEquals(401, exception.getStatusCode().value());
    }

    @Test
    void shouldAllowInternalWhenConfigured() {
        var auth = new UsernamePasswordAuthenticationToken(
                "internal-service",
                null,
                List.of(() -> "ROLE_INTERNAL")
        );

        assertDoesNotThrow(() -> guard.requireUserAccess(auth, 99L, true));
    }

    @Test
    void shouldRejectDifferentUser() {
        var auth = new UsernamePasswordAuthenticationToken("1", null, List.of(() -> "ROLE_TITIPER"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> guard.requireUserAccess(auth, 2L, false)
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void shouldAllowMatchingUser() {
        var auth = new UsernamePasswordAuthenticationToken("7", null, List.of(() -> "ROLE_TITIPER"));

        assertDoesNotThrow(() -> guard.requireUserAccess(auth, 7L, false));
    }

    @Test
    void shouldRejectInternalPrincipalWhenInternalAccessIsNotAllowed() {
        var auth = new UsernamePasswordAuthenticationToken("internal-service", null, List.of(() -> "ROLE_INTERNAL"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> guard.requireUserAccess(auth, 7L, false)
        );

        assertEquals(403, exception.getStatusCode().value());
    }
}
