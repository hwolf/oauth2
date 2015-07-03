package hw.oauth2.authentication;

import java.util.Map;

import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class MyPasswordEncoder implements PasswordEncoder {

    private static final String PLAIN = "plain";

    private final String prefered;
    private final Map<String, PasswordEncoder> encoders;

    public static Builder builder() {
        return new Builder();
    }

    private MyPasswordEncoder(String prefered, Map<String, PasswordEncoder> encoders) {
        this.prefered = prefered;
        this.encoders = ImmutableMap.copyOf(encoders);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return prefered + '$' + encoders.get(prefered).encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String[] parts = splitPassword(encodedPassword);
        return encoders.get(parts[0]).matches(rawPassword, parts[1]);
    }

    private String[] splitPassword(String encodedPassword) {
        int posColon = encodedPassword.indexOf('$');
        if (posColon < 0) {
            return new String[] { PLAIN, encodedPassword };
        }
        return new String[] { encodedPassword.substring(0, posColon), encodedPassword.substring(posColon + 1) };
    }

    public static class Builder {

        private String prefered;
        private final Map<String, PasswordEncoder> encoders = Maps.newHashMap();

        public Builder withPreferedEncoder(String name, PasswordEncoder encoder) {
            checkState(prefered == null);
            prefered = name;
            return withEncoder(name, encoder);
        }

        public Builder withEncoder(String name, PasswordEncoder encoder) {
            checkState(!encoders.containsKey(name));
            encoders.put(checkNotNull(name), checkNotNull(encoder));
            return this;
        }

        public PasswordEncoder build() {
            checkState(prefered != null);
            checkState(!encoders.isEmpty());
            if (!encoders.containsKey(PLAIN)) {
                encoders.put(PLAIN, NoOpPasswordEncoder.getInstance());
            }
            return new MyPasswordEncoder(prefered, encoders);
        }
    }
}
