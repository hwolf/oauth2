/*
 * Copyright 2015 H. Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oauth2.entities;

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
