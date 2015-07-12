package hw.oauth2.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

@Entity
@Table(name = "t_refresh_tokens")
public class RefreshToken {

    @Id
    private String tokenId;

    @Lob
    private byte[] token;

    @Lob
    private byte[] authentication;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public OAuth2RefreshToken getToken() {
        return SerializationUtils.deserialize(token);
    }

    public void setToken(OAuth2RefreshToken refreshToken) {
        token = SerializationUtils.serialize(refreshToken);
    }

    public OAuth2Authentication getAuthentication() {
        return SerializationUtils.deserialize(authentication);
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = SerializationUtils.serialize(authentication);
    }
}
