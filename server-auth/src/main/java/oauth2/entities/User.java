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
package oauth2.entities;

import java.time.Instant;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.util.StringUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import oauth2.entities.converters.InstantConverter;

@Entity
@Table(name = "t_users")
@Getter
@Setter
public class User {

    @Id
    private String userId;

    private String password;

    @Convert(converter = InstantConverter.class)
    private Instant passwordExpiresAt;

    @ElementCollection
    @CollectionTable(name = "t_user_entries", joinColumns = @JoinColumn(name = "user_id") )
    @Setter(AccessLevel.NONE)
    private Collection<Entry> entries;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @Setter(AccessLevel.NONE)
    private LoginStatus loginStatus;

    public LoginStatus getLoginStatus() {
        if (loginStatus == null) {
            loginStatus = new LoginStatus(userId);
        }
        return loginStatus;
    }

    public boolean isEnabled() {
        return StringUtils.hasText(password);
    }

    public boolean isPasswordExpired() {
        return passwordExpiresAt == null || !passwordExpiresAt.isAfter(Instant.now());
    }

    public boolean isAccountLocked() {
        return getLoginStatus().getFailedLoginAttempts() >= 3;
    }

    public Collection<Entry> getEntries() {
        if (entries == null) {
            return ImmutableSet.of();
        }
        return ImmutableSet.copyOf(entries);
    }

    public void addEntry(String name, String value) {
        if (entries == null) {
            entries = Sets.newHashSet();
        }
        entries.add(Entry.create(name, value));
    }

    public void removeEntry(String name, String value) {
        if (entries == null) {
            return;
        }
        entries.remove(Entry.create(name, value));
    }
}
