/*
 * Copyright 2015 H. Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oauth2.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;

import oauth2.authentication.UserAuthenticationStrategy;
import oauth2.entities.User;
import oauth2.entities.UserRepository;

@Transactional
public class ChangePasswordService {

    private final UserRepository userRepository;
    private final UserAuthenticationStrategy authenticationStrategy;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordService(UserRepository userRepository, UserAuthenticationStrategy authenticationStrategy,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationStrategy = authenticationStrategy;
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
        authenticationStrategy.authenticate(user, oldPassword);

        // Step 4: Validate new password.
        changePassword(user, newPassword);

        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordExpiresAt(Instant.now().plus(90, ChronoUnit.DAYS));
    }
}
