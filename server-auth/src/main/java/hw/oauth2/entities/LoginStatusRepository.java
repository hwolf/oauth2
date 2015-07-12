package hw.oauth2.entities;

import java.time.Instant;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface LoginStatusRepository extends Repository<LoginStatus, String> {

    @Modifying(clearAutomatically = true)
    @Query("update LoginStatus " //
            + "set failedLoginAttempts = 0, lastSuccessfulLogin = :when " //
            + "where userId = :userId")
    int loginSuccessful(@Param("userId") String userId, @Param("when") Instant when);

    @Modifying(clearAutomatically = true)
    @Query("update LoginStatus " //
            + "set failedLoginAttempts = failedLoginAttempts+1, lastFailedLogin = :when " //
            + "where userId = :userId")
    int loginFailed(@Param("userId") String userId, @Param("when") Instant when);
}
