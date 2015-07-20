package hw.oauth2.authentication.users;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import hw.oauth2.Roles;

public class UserDetailsBuilder {

    private String userId;
    private String password;
    private boolean accountEnabled;
    private boolean passwordExpired;
    private boolean accountLocked;
    private final Set<String> authorities = Sets.newHashSet();

    public String userId() {
        return userId;
    }

    public UserDetailsBuilder userId(String userId) {
        this.userId = userId;
        return this;
    }

    public UserDetailsBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserDetailsBuilder accountEnabled(boolean accountEnabled) {
        this.accountEnabled = accountEnabled;
        return this;
    }

    public UserDetailsBuilder passwordExpired(boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
        return this;
    }

    public UserDetailsBuilder accountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
        return this;
    }

    public UserDetailsBuilder withAuthority(String authority) {
        authorities.add(authority);
        return this;
    }

    public UserDetailsBuilder withRole(String role) {
        authorities.add("ROLE_" + role);
        return this;
    }

    public UserDetails build() {
        return new User(userId, password, accountEnabled, true, true, true, mapAuthorities());
    }

    private Collection<? extends GrantedAuthority> mapAuthorities() {
        Preconditions.checkState(!authorities.contains("ROLE_" + Roles.ACCOUNT_LOCKED)
                && !authorities.contains("ROLE_" + Roles.MUST_CHANGE_PASSWORD));
        if (accountLocked) {
            return ImmutableSet.of(new SimpleGrantedAuthority("ROLE_" + Roles.ACCOUNT_LOCKED));
        }
        if (passwordExpired) {
            return ImmutableSet.of(new SimpleGrantedAuthority("ROLE_" + Roles.MUST_CHANGE_PASSWORD));
        }
        authorities.add("ROLE_" + Roles.AUTHENTICATED);
        return authorities.stream().map(authority -> new SimpleGrantedAuthority(authority)).collect(Collectors.toSet());
    }
}
