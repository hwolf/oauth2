package hw.oauth2.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;

import hw.oauth2.authentication.MyAuthenticationProvider;
import hw.oauth2.entities.User;
import hw.oauth2.entities.UserRepository;

@Transactional
public class ChangePasswordService {

    private final UserRepository userRepository;
    private final MyAuthenticationProvider authenticationProvider;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordService(UserRepository userRepository, MyAuthenticationProvider authenticationProvider,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationProvider = authenticationProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasAnyRole('ROLE_AUTHENTICATED', 'ROLE_MUST_CHANGE_PASSWORD')")
    public void changePassword(String userId, String oldPassword, String newPassword)
            throws ChangePasswordException, AuthenticationException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Step 1: Check user id against authenticated user
        if (!Objects.equal(userId, authentication.getName())) {
            throw new ChangePasswordException("Invalid user id");
        }

        // Step 2: Search user in repository
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new ChangePasswordException("User not found");
        }

        // Step 3: Validate if old password is correct.
        authenticationProvider.authenticate(user, oldPassword);

        // Step 4: Validate new password.
        changePassword(user, newPassword);

        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordExpiresAt(Instant.now().plus(90, ChronoUnit.DAYS));
    }
}
