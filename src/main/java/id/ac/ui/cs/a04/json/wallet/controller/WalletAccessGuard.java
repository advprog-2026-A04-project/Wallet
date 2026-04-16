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
}
