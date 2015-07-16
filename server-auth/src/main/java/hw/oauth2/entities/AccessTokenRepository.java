package hw.oauth2.entities;

import java.util.Collection;

import org.springframework.data.repository.Repository;

public interface AccessTokenRepository extends Repository<AccessToken, String> {

    AccessToken findByAuthenticationId(String authenticationId);

    AccessToken findByTokenId(String tokenId);

    AccessToken findByRefreshToken(String refreshToken);

    Collection<AccessToken> findByClientId(String clientId);

    Collection<AccessToken> findByClientIdAndUserId(String clientId, String userId);

    AccessToken save(AccessToken accessToken);

    void delete(AccessToken accessToken);
}
