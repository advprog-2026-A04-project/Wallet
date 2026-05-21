package id.ac.ui.cs.a04.json.wallet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class WalletAccessGuard {

    public void requireUserAccess(Authentication authentication, Long userId, boolean allowInternal) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required.");
        }

        boolean internal = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_INTERNAL"::equals);

        if (allowInternal && internal) {
            return;
        }

        if (!String.valueOf(userId).equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You may only access your own wallet.");
        }
    }

    public void requireAdminOrInternal(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required.");
        }

        boolean allowed = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority) || "ROLE_INTERNAL".equals(authority));
        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin or internal service access is required.");
        }
    }

    public void requireMarkPermission(Authentication authentication, Long requestUserId) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required.");
        }

        boolean isInternal = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_INTERNAL"::equals);
        if (isInternal) {
            return;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied.");
        }

        if (String.valueOf(requestUserId).equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot mark your own top-up/withdrawal requests.");
        }
    }
}
