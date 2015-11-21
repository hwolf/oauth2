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
package oauth2.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.google.common.base.Preconditions.checkNotNull;

import oauth2.entities.User;

public class DefaultUserAuthenticationStrategy implements UserAuthenticationStrategy, MessageSourceAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserAuthenticationStrategy.class);

    private final PasswordEncoder passwordEncoder;

    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public DefaultUserAuthenticationStrategy(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public void authenticate(User user, Object credentials) {
        checkNotNull(user);

        String userId = user.getUserId();
        if (!user.isEnabled()) {
            LOGGER.debug("User {} is disabled", userId);
            throw new DisabledException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
        }
        if (user.isAccountLocked()) {
            LOGGER.debug("User account {} is locked", userId);
            throw new LockedException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
        }
        if (credentials == null) {
            LOGGER.debug("Authentication for user {} failed: No credentials provided", userId);
            throw new BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        if (!passwordEncoder.matches(credentials.toString(), user.getPassword())) {
            LOGGER.debug("Authentication for user {} failed: Password does not match stored value", userId);
            throw new BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }
}
