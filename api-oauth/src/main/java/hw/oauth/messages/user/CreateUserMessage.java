package hw.oauth.messages.user;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import hw.oauth.messages.UserMessage;

public class CreateUserMessage implements UserMessage {

    private final String userId;
    private final String password;
    private final Set<String> authorities;

    public static Builder builder(String userId) {
        return new Builder(userId);
    }

    private CreateUserMessage(Builder builder) {
        userId = builder.userId;
        password = builder.password;
        authorities = builder.authorities.build();
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public static class Builder {

        private final String userId;
        private String password;
        private final ImmutableSet.Builder<String> authorities = ImmutableSet.builder();

        public Builder(String userId) {
            this.userId = userId;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder withAuthority(String authority) {
            authorities.add(authority);
            return this;
        }

        public Builder withAuthorities(String... authorities) {
            this.authorities.add(authorities);
            return this;
        }

        public Builder withRole(String role) {
            authorities.add("ROLE_" + role);
            return this;
        }

        public Builder withRoles(String... roles) {
            for (String role : roles) {
                withRole(role);
            }
            return this;
        }

        public CreateUserMessage build() {
            return new CreateUserMessage(this);
        }
    }
}
