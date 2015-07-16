package hw.oauth2.entities;

import org.springframework.data.repository.Repository;

public interface RefreshTokenRepository extends Repository<RefreshToken, String> {

    RefreshToken findByTokenId(String tokenId);

    RefreshToken save(RefreshToken refreshToken);

    void delete(RefreshToken refreshToken);
}
