package id.ac.ui.cs.a04.json.wallet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionRequestMarkGuardTest {

    private final TransactionRequestMarkGuard guard = new TransactionRequestMarkGuard();

    @Test
    void shouldRejectMissingAuthentication() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> guard.requireMarkPermission(null, 2L)
        );

        assertEquals(401, exception.getStatusCode().value());
    }

    @Test
    void shouldRejectNonAdmin() {
        var auth = new UsernamePasswordAuthenticationToken(
                "1", null, List.of(() -> "ROLE_TITIPER"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> guard.requireMarkPermission(auth, 2L)
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void shouldRejectSelfMark() {
        var auth = new UsernamePasswordAuthenticationToken(
                "7", null, List.of(() -> "ROLE_ADMIN"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> guard.requireMarkPermission(auth, 7L)
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void shouldAllowAdminAndNotSelfMark() {
        var auth = new UsernamePasswordAuthenticationToken(
                "7", null, List.of(() -> "ROLE_ADMIN"));

        assertDoesNotThrow(() -> guard.requireMarkPermission(auth, 2L));
    }
}
