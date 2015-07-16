package hw.oauth2.entities.user;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;

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
import javax.persistence.Transient;

import org.springframework.util.StringUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import hw.jpa.converters.InstantConverter;
import hw.oauth2.entities.Entry;
import hw.oauth2.entities.LoginStatus;

@Entity
@Table(name = "t_users")
public class User {

    @Id
    private String userId;

    @Transient
    private String oldUserId;

    private String password;

    @Convert(converter = InstantConverter.class)
    private Instant passwordExpiresAt;

    @ElementCollection
    @CollectionTable(name = "t_user_entries", joinColumns = @JoinColumn(name = "user_id") )
    private Collection<Entry> entries;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private LoginStatus loginStatus;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (oldUserId == null) {
            oldUserId = this.userId;
        }
        this.userId = userId;
    }

    String getOldUserId() {
        return oldUserId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getPasswordExpiresAt() {
        return passwordExpiresAt;
    }

    public void setPasswordExpiresAt(Instant passwordExpiredAt) {
        passwordExpiresAt = passwordExpiredAt;
    }

    public Set<String> getAuthorities() {
        return Entry.filterEntriesByName("AUTHORITY", entries);
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
        return getLoginStatus().getFailedLoginAttempts() > 3;
    }

}
