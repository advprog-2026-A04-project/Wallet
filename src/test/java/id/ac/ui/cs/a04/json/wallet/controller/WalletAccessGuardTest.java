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

    @Test
    void shouldRejectMissingAuthenticationForAdminOrInternalAccess() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> guard.requireAdminOrInternal(null)
        );

        assertEquals(401, exception.getStatusCode().value());
    }

    @Test
    void shouldAllowAdminForAdminOrInternalAccess() {
        var auth = new UsernamePasswordAuthenticationToken("9001", null, List.of(() -> "ROLE_ADMIN"));

        assertDoesNotThrow(() -> guard.requireAdminOrInternal(auth));
    }

    @Test
    void shouldAllowInternalForAdminOrInternalAccess() {
        var auth = new UsernamePasswordAuthenticationToken("internal-service", null, List.of(() -> "ROLE_INTERNAL"));

        assertDoesNotThrow(() -> guard.requireAdminOrInternal(auth));
    }

    @Test
    void shouldRejectRegularUserForAdminOrInternalAccess() {
        var auth = new UsernamePasswordAuthenticationToken("7", null, List.of(() -> "ROLE_TITIPER"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> guard.requireAdminOrInternal(auth)
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void requireMarkPermissionShouldRejectMissingAuthentication() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> guard.requireMarkPermission(null, 2L)
        );

        assertEquals(401, exception.getStatusCode().value());
    }

    @Test
    void requireMarkPermissionShouldRejectNonAdmin() {
        var auth = new UsernamePasswordAuthenticationToken(
                "1", null, List.of(() -> "ROLE_TITIPER"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> guard.requireMarkPermission(auth, 2L)
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void requireMarkPermissionShouldRejectSelfMark() {
        var auth = new UsernamePasswordAuthenticationToken(
                "7", null, List.of(() -> "ROLE_ADMIN"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> guard.requireMarkPermission(auth, 7L)
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void requireMarkPermissionShouldAllowAdminAndNotSelfMark() {
        var auth = new UsernamePasswordAuthenticationToken(
                "7", null, List.of(() -> "ROLE_ADMIN"));

        assertDoesNotThrow(() -> guard.requireMarkPermission(auth, 2L));
    }

    @Test
    void requireMarkPermissionShouldAllowInternalService() {
        var auth = new UsernamePasswordAuthenticationToken("internal-service", null, List.of(() -> "ROLE_INTERNAL"));

        assertDoesNotThrow(() -> guard.requireMarkPermission(auth, 2L));
    }
}
