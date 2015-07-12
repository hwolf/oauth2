package hw.oauth2.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

@Entity
@Table(name = "t_access_tokens")
public class AccessToken {

    @Id
    private String authenticationId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String clientId;

    private String refreshToken;

    private String tokenId;

    @Lob
    private byte[] token;

    @Lob
    private byte[] authentication;

    public String getAuthenticationId() {
        return authenticationId;
    }

    public void setAuthenticationId(String authenticationId) {
        this.authenticationId = authenticationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public OAuth2AccessToken getToken() {
        return SerializationUtils.deserialize(token);
    }

    public void setToken(OAuth2AccessToken token) {
        this.token = SerializationUtils.serialize(token);
    }

    public OAuth2Authentication getAuthentication() {
        return SerializationUtils.deserialize(authentication);
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = SerializationUtils.serialize(authentication);
    }
}
