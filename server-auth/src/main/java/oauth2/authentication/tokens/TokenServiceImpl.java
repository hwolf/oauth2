package oauth2.authentication.tokens;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.google.common.base.Objects;

import oauth2.entities.AccessToken;
import oauth2.entities.AccessTokenRepository;
import oauth2.entities.RefreshToken;
import oauth2.entities.RefreshTokenRepository;
import oauth2.utils.MessageDigestUtils;

public class TokenServiceImpl implements TokenStore {

    private final AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenServiceImpl(AccessTokenRepository accessTokenRepository,
            RefreshTokenRepository refreshTokenRepository) {
        this.accessTokenRepository = accessTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String key = authenticationKeyGenerator.extractKey(authentication);
        AccessToken entity = accessTokenRepository.findByAuthenticationId(key);
        if (entity == null) {
            return null;
        }
        OAuth2AccessToken accessToken = entity.getToken();
        if (accessToken == null) {
            return null;
        }
        OAuth2Authentication authFromAccessToken = readAuthentication(accessToken.getValue());
        if (authFromAccessToken == null
                || !Objects.equal(key, authenticationKeyGenerator.extractKey(authFromAccessToken))) {
            accessTokenRepository.delete(entity);
            // Keep the store consistent (maybe the same user is represented by this
            // authentication but the details have changed)
            storeAccessToken(accessToken, authentication);
        }
        return accessToken;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String key = authenticationKeyGenerator.extractKey(authentication);
        AccessToken oldEntity = accessTokenRepository.findByAuthenticationId(key);
        if (oldEntity != null) {
            accessTokenRepository.delete(oldEntity);
        }
        String refreshToken = null;
        if (token.getRefreshToken() != null) {
            refreshToken = token.getRefreshToken().getValue();
        }
        AccessToken entity = new AccessToken();
        entity.setTokenId(extractTokenKey(token.getValue()));
        entity.setToken(token);
        entity.setAuthenticationId(key);
        entity.setUserId(authentication.isClientOnly() ? null : authentication.getName());
        entity.setClientId(authentication.getOAuth2Request().getClientId());
        entity.setAuthentication(authentication);
        entity.setRefreshToken(extractTokenKey(refreshToken));
        accessTokenRepository.save(entity);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        AccessToken entity = accessTokenRepository.findByTokenId(extractTokenKey(tokenValue));
        if (entity == null) {
            return null;
        }
        return entity.getToken();
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        AccessToken entity = accessTokenRepository.findByTokenId(extractTokenKey(token.getValue()));
        if (entity != null) {
            accessTokenRepository.delete(entity);
        }
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String tokenValue) {
        AccessToken entity = accessTokenRepository.findByTokenId(extractTokenKey(tokenValue));
        if (entity == null) {
            return null;
        }
        return entity.getAuthentication();
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        RefreshToken entity = new RefreshToken();
        entity.setTokenId(extractTokenKey(refreshToken.getValue()));
        entity.setToken(refreshToken);
        entity.setAuthentication(authentication);
        refreshTokenRepository.save(entity);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String token) {
        String tokenId = extractTokenKey(token);
        RefreshToken entity = refreshTokenRepository.findByTokenId(tokenId);
        if (entity == null) {
            return null;
        }
        return entity.getToken();
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        String tokenId = extractTokenKey(token.getValue());
        RefreshToken entity = refreshTokenRepository.findByTokenId(tokenId);
        if (entity == null) {
            return null;
        }
        return entity.getAuthentication();
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        String tokenId = extractTokenKey(token.getValue());
        RefreshToken entity = refreshTokenRepository.findByTokenId(tokenId);
        if (entity != null) {
            refreshTokenRepository.delete(entity);
        }
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        AccessToken entity = accessTokenRepository.findByRefreshToken(refreshToken.getValue());
        if (entity != null) {
            accessTokenRepository.delete(entity);
        }
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return accessTokenRepository.findByClientId(clientId) //
                .stream() //
                .map(entity -> entity.getToken()) //
                .collect(Collectors.toList());
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userId) {
        return accessTokenRepository.findByClientIdAndUserId(clientId, userId) //
                .stream() //
                .map(entity -> entity.getToken()) //
                .collect(Collectors.toList());
    }

    protected String extractTokenKey(String value) {
        if (value == null) {
            return null;
        }
        byte[] bytes = MessageDigestUtils.digest(value, Charset.forName("UTF-8"));
        return String.format("%032x", new BigInteger(1, bytes));
    }
}
