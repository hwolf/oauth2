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

import java.time.Instant;
import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableSet;

import static com.google.common.base.Preconditions.checkNotNull;

import oauth2.Roles;
import oauth2.entities.User;
import oauth2.entities.UserRepository;

public class UserAuthenticationProvider implements AuthenticationProvider, MessageSourceAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthenticationProvider.class);

    private final UserRepository userRepository;
    private final UserAuthenticationStrategy authenticationStrategy;

    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public UserAuthenticationProvider(UserRepository userRepository,
            UserAuthenticationStrategy authenticationStrategy) {
        this.userRepository = userRepository;
        this.authenticationStrategy = authenticationStrategy;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    @Transactional(noRollbackFor = AuthenticationException.class)
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        checkNotNull(authentication);
        String userId = authentication.getName();
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            LOGGER.debug("User {} not found", userId);
            throw new BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        try {
            authenticationStrategy.authenticate(user, authentication.getCredentials());
            user.getLoginStatus().loginSuccessful(Instant.now());
            Set<GrantedAuthority> authorities = mapAuthorities(user);
            return createSuccessAuthentication(authentication.getPrincipal(), authentication, authorities);
        } catch (BadCredentialsException ex) {
            user.getLoginStatus().loginFailed(Instant.now());
            throw ex;
        }
    }

    protected Set<GrantedAuthority> mapAuthorities(User user) {
        Set<GrantedAuthority> authorities = user.getAuthorities();
        if (user.isPasswordExpired()) {
            LOGGER.debug("User {}: Credentials have expired", user.getUserId());
            authorities = ImmutableSet.of(new SimpleGrantedAuthority("ROLE_" + Roles.MUST_CHANGE_PASSWORD));
        }
        return authorities;
    }

    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
            Collection<GrantedAuthority> authorities) {

        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(principal,
                authentication.getCredentials(), authorities);
        result.setDetails(authentication.getDetails());
        return result;
    }
}
