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
package oauth2.password;

import java.util.Map;

import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * A password encoder which supports several password encoders. The encoded password is prefixed by
 * the name of the used password encode. The idea is to support better password encoder in the
 * future but support old password encoders for backward compatibility.
 */
public final class MyPasswordEncoder implements PasswordEncoder {

    static final char SEPARATOR = '\t';

    private static final String PLAIN = "plain";

    private final String nameOfDefaultEncoder;
    private final Map<String, PasswordEncoder> encoders;

    public static Builder builder() {
        return new Builder();
    }

    private MyPasswordEncoder(String nameOfDefaultEncoder, Map<String, PasswordEncoder> encoders) {
        this.nameOfDefaultEncoder = nameOfDefaultEncoder;
        this.encoders = ImmutableMap.copyOf(encoders);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return nameOfDefaultEncoder + SEPARATOR + encoders.get(nameOfDefaultEncoder).encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String[] parts = splitPassword(encodedPassword);
        return encoders.get(parts[0]).matches(rawPassword, parts[1]);
    }

    private String[] splitPassword(String encodedPassword) {
        int posSeparator = encodedPassword.indexOf(SEPARATOR);
        if (posSeparator < 0) {
            return new String[] { PLAIN, encodedPassword };
        }
        return new String[] { encodedPassword.substring(0, posSeparator), encodedPassword.substring(posSeparator + 1) };
    }

    public static class Builder {

        private String prefered;
        private final Map<String, PasswordEncoder> encoders = Maps.newHashMap();

        public Builder defaultEncoder(String name, PasswordEncoder encoder) {
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
