package hw.oauth2.entities;

import java.util.Collection;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.google.common.collect.ImmutableSet;

@Entity
@Table(name = "t_clients")
public class Client {

    @Id
    private String clientId;

    @Column(nullable = false)
    private String clientSecret;

    @ElementCollection
    @CollectionTable(name = "t_client_entries", joinColumns = @JoinColumn(name = "client_id") )
    private Collection<Entry> entries;

    private Integer accessTokenValidity;
    private Integer refreshTokenValidity;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String userId) {
        clientId = userId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String password) {
        clientSecret = password;
    }

    public Collection<Entry> getEntries() {
        if (entries == null) {
            return ImmutableSet.of();
        }
        return ImmutableSet.copyOf(entries);
    }

    public Integer getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setAccessTokenValidity(Integer accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public Integer getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(Integer refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }
}
