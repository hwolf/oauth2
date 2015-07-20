package hw.oauth2.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import com.google.common.base.Objects;

import hw.oauth2.entities.User;
import hw.oauth2.entities.UserRepository;

@Transactional
public class ChangePasswordService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public ChangePasswordService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasAnyRole('ROLE_AUTHENTICATED', 'ROLE_MUST_CHANGE_PASSWORD')")
    public void changePassword(String userId, String oldPassword, String newPassword, Errors errors) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!Objects.equal(userId, authentication.getName())) {
            errors.reject("BadCredentials", "Bad credentials");
            return;
        }

        // Step 1: Search user in repository
        User user = loadUserByUserId(userId, errors);
        if (user == null) {
            return;
        }

        // Step 2: Validate if old password is correct.
        if (!authenticate(user, oldPassword, errors)) {
            return;
        }

        // Step 3: Validate new password.
        changePassword(user, newPassword);

        authentication.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private User loadUserByUserId(String userId, Errors errors) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            errors.reject("BadCredentials", "Bad credentials");
            return null;
        }
        if (user.isAccountLocked()) {
            errors.reject("UserAccountLocked", "User account is locked");
            return null;
        }
        if (!user.isEnabled()) {
            errors.reject("UserDisabled", "User is disabled");
            return null;
        }
        return user;
    }

    private boolean authenticate(User user, String password, Errors errors) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            errors.reject("BadCredentials", "Bad credentials");
            return false;
        }
        return true;
    }

    private void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordExpiresAt(Instant.now().plus(90, ChronoUnit.DAYS));
    }
}
