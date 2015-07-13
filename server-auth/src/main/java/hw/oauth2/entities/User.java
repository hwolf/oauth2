package hw.oauth2.entities;

import java.time.Instant;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import hw.jpa.converters.InstantConverter;

@Entity
@Table(name = "t_users")
public class User {

    @Id
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
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
        this.userId = userId;
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
        return loginStatus;
    }

    public void setLoginStatus() {
        loginStatus = new LoginStatus(userId);
    }
}
