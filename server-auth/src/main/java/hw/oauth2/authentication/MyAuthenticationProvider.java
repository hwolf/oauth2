package hw.oauth2.authentication;

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
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;

import hw.oauth2.Roles;
import hw.oauth2.entities.User;
import hw.oauth2.entities.UserRepository;

// TODO: Better name, unit tests
public class MyAuthenticationProvider implements AuthenticationProvider, MessageSourceAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyAuthenticationProvider.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public MyAuthenticationProvider(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
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
        String userId = authentication.getName();
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            LOGGER.debug("User '" + userId + "' not found");
            throw new BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        if (!user.isEnabled()) {
            LOGGER.debug("User account is disabled");
            throw new DisabledException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
        }
        if (user.isAccountLocked()) {
            LOGGER.debug("User account is locked");
            throw new LockedException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
        }
        if (authentication.getCredentials() == null) {
            LOGGER.debug("Authentication failed: no credentials provided");
            throw loginFailedBecauseOfBadCredentials(user);
        }
        if (!passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
            LOGGER.debug("Authentication failed: password does not match stored value");
            throw loginFailedBecauseOfBadCredentials(user);
        }

        Set<GrantedAuthority> authorities = user.getAuthorities();
        if (user.isPasswordExpired()) {
            LOGGER.debug("User account credentials have expired");
            authorities = ImmutableSet.of(new SimpleGrantedAuthority("ROLE_" + Roles.MUST_CHANGE_PASSWORD));
        }
        return loginSuccessful(authentication, user, authorities);
    }

    private BadCredentialsException loginFailedBecauseOfBadCredentials(User user) {
        return loginFailedBecauseOfBadCredentials(user, Instant.now());
    }

    @VisibleForTesting
    BadCredentialsException loginFailedBecauseOfBadCredentials(User user, Instant when) {
        user.getLoginStatus().loginFailed(when);
        return new BadCredentialsException(
                messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
    }

    private Authentication loginSuccessful(Authentication authentication, User user,
            Collection<GrantedAuthority> authorities) {
        return loginSuccessful(authentication, user, authorities, Instant.now());
    }

    @VisibleForTesting
    Authentication loginSuccessful(Authentication authentication, User user, Collection<GrantedAuthority> authorities,
            Instant when) {
        user.getLoginStatus().loginSuccessful(when);
        return createSuccessAuthentication(authentication.getPrincipal(), authentication, authorities);
    }

    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
            Collection<GrantedAuthority> authorities) {

        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(principal,
                authentication.getCredentials(), authorities);
        result.setDetails(authentication.getDetails());
        return result;
    }
}
