package hw.oauth2.tokens;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

import hw.oauth2.security.MessageDigestUtils;

public class TokenServiceImpl implements TokenStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    private final JdbcTemplate jdbcTemplate;

    public TokenServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = null;

        String key = authenticationKeyGenerator.extractKey(authentication);
        try {
            accessToken = jdbcTemplate.queryForObject(TokenServiceSqls.SQL_SELECT_ACCESS_TOKEN_FROM_AUTHENTICATION,
                    (rs, rowNum) -> {
                        return deserializeAccessToken(rs.getBytes(2));
                    } , key);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug("Failed to find access token for authentication {}", authentication);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Could not extract access token for authentication " + authentication, e);
        }

        if (accessToken != null
                && !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
            removeAccessToken(accessToken.getValue());
            // Keep the store consistent (maybe the same user is represented by this authentication
            // but the details have changed)
            storeAccessToken(accessToken, authentication);
        }
        return accessToken;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String refreshToken = null;
        if (token.getRefreshToken() != null) {
            refreshToken = token.getRefreshToken().getValue();
        }

        if (readAccessToken(token.getValue()) != null) {
            removeAccessToken(token.getValue());
        }

        jdbcTemplate.update(TokenServiceSqls.SQL_INSERT_ACCESS_TOKEN,
                new Object[] { extractTokenKey(token.getValue()), new SqlLobValue(serializeAccessToken(token)),
                        authenticationKeyGenerator.extractKey(authentication),
                        authentication.isClientOnly() ? null : authentication.getName(),
                        authentication.getOAuth2Request().getClientId(),
                        new SqlLobValue(serializeAuthentication(authentication)), extractTokenKey(refreshToken) },
                new int[] { Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB,
                        Types.VARCHAR });
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken accessToken = null;

        try {
            accessToken = jdbcTemplate.queryForObject(TokenServiceSqls.SQL_SELECT_ACCESS_TOKEN, (rs, rowNum) -> {
                return deserializeAccessToken(rs.getBytes(2));
            } , extractTokenKey(tokenValue));
        } catch (EmptyResultDataAccessException e) {
            LOGGER.info("Failed to find access token for token {}", tokenValue);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Failed to deserialize access token for " + tokenValue, e);
            removeAccessToken(tokenValue);
        }
        return accessToken;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        removeAccessToken(token.getValue());
    }

    public void removeAccessToken(String tokenValue) {
        jdbcTemplate.update(TokenServiceSqls.SQL_DELETE_ACCESS_TOKEN, extractTokenKey(tokenValue));
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        OAuth2Authentication authentication = null;
        try {
            authentication = jdbcTemplate.queryForObject(TokenServiceSqls.SQL_SELECT_ACCESS_TOKEN_AUTHENTICATION,
                    (rs, rowNum) -> {
                        return deserializeAuthentication(rs.getBytes(2));
                    } , extractTokenKey(token));
        } catch (EmptyResultDataAccessException e) {
            LOGGER.info("Failed to find access token for token {}", token);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Failed to deserialize authentication for " + token, e);
            removeAccessToken(token);
        }
        return authentication;
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        jdbcTemplate.update(TokenServiceSqls.SQL_INSERT_REFRESH_TOKEN,
                new Object[] { extractTokenKey(refreshToken.getValue()),
                        new SqlLobValue(serializeRefreshToken(refreshToken)),
                        new SqlLobValue(serializeAuthentication(authentication)) },
                new int[] { Types.VARCHAR, Types.BLOB, Types.BLOB });
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String token) {
        OAuth2RefreshToken refreshToken = null;

        try {
            refreshToken = jdbcTemplate.queryForObject(TokenServiceSqls.SQL_SELECT_REFRESH_TOKENT, (rs, rowNum) -> {
                return deserializeRefreshToken(rs.getBytes(2));
            } , extractTokenKey(token));
        } catch (EmptyResultDataAccessException e) {
            LOGGER.info("Failed to find refresh token for token {}", token);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Failed to deserialize refresh token for token " + token, e);
            removeRefreshToken(token);
        }

        return refreshToken;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        removeRefreshToken(token.getValue());
    }

    public void removeRefreshToken(String token) {
        jdbcTemplate.update(TokenServiceSqls.SQL_DELETE_REFRESH_TOKEN, extractTokenKey(token));
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(String value) {
        OAuth2Authentication authentication = null;

        try {
            authentication = jdbcTemplate.queryForObject(TokenServiceSqls.SQL_SELECT_REFRESH_TOKEN_AUTHENTICATION,
                    (rs, rowNum) -> {
                        return deserializeAuthentication(rs.getBytes(2));
                    } , extractTokenKey(value));
        } catch (EmptyResultDataAccessException e) {
            LOGGER.info("Failed to find access token for token {}", value);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Failed to deserialize access token for " + value, e);
            removeRefreshToken(value);
        }

        return authentication;
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    public void removeAccessTokenUsingRefreshToken(String refreshToken) {
        jdbcTemplate.update(TokenServiceSqls.SQL_DELETE_FROM_REFRESH_TOKEN,
                new Object[] { extractTokenKey(refreshToken) }, new int[] { Types.VARCHAR });
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<>();

        try {
            accessTokens = jdbcTemplate.query(TokenServiceSqls.SQL_SELECT_DEFAULT_TOKENS_FROM_CLIENT,
                    new SafeAccessTokenRowMapper(), clientId);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.info("Failed to find access token for clientId {}", clientId);
        }
        accessTokens = removeNulls(accessTokens);

        return accessTokens;
    }

    public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<>();

        try {
            accessTokens = jdbcTemplate.query(TokenServiceSqls.SQL_SELECT_DEFAULT_TOKENS_FROM_USERNAME,
                    new SafeAccessTokenRowMapper(), userName);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.info("Failed to find access token for userName{}", userName);
        }
        accessTokens = removeNulls(accessTokens);

        return accessTokens;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();

        try {
            accessTokens = jdbcTemplate.query(TokenServiceSqls.SQL_SELECT_DEFAULT_TOKENS_FROM_USERNAME_AND_CLIENT,
                    new SafeAccessTokenRowMapper(), userName, clientId);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.info("Failed to find access token for clientId {} and userName {}", clientId, userName);
        }
        accessTokens = removeNulls(accessTokens);

        return accessTokens;
    }

    private List<OAuth2AccessToken> removeNulls(List<OAuth2AccessToken> accessTokens) {
        List<OAuth2AccessToken> tokens = new ArrayList<OAuth2AccessToken>();
        for (OAuth2AccessToken token : accessTokens) {
            if (token != null) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    protected String extractTokenKey(String value) {
        if (value == null) {
            return null;
        }
        byte[] bytes = MessageDigestUtils.digest(value, Charset.forName("UTF-8"));
        return String.format("%032x", new BigInteger(1, bytes));
    }

    private final class SafeAccessTokenRowMapper implements RowMapper<OAuth2AccessToken> {

        @Override
        public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                return deserializeAccessToken(rs.getBytes(2));
            } catch (IllegalArgumentException e) {
                String token = rs.getString(1);
                jdbcTemplate.update(TokenServiceSqls.SQL_DELETE_ACCESS_TOKEN, token);
                return null;
            }
        }
    }

    protected byte[] serializeAccessToken(OAuth2AccessToken token) {
        return SerializationUtils.serialize(token);
    }

    protected byte[] serializeRefreshToken(OAuth2RefreshToken token) {
        return SerializationUtils.serialize(token);
    }

    protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
        return SerializationUtils.serialize(authentication);
    }

    protected OAuth2AccessToken deserializeAccessToken(byte[] token) {
        return SerializationUtils.deserialize(token);
    }

    protected OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
        return SerializationUtils.deserialize(token);
    }

    protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
        return SerializationUtils.deserialize(authentication);
    }
}
