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

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import oauth2.jpa.converters.InstantConverter;

@Entity
@Table(name = "t_login_status")
@Getter
public class LoginStatus {

    @Id
    private String userId;

    private int failedLoginAttempts;

    @Convert(converter = InstantConverter.class)
    private Instant lastSuccessfulLogin;

    @Convert(converter = InstantConverter.class)
    private Instant lastFailedLogin;

    LoginStatus() {
    }

    public LoginStatus(String userId) {
        this.userId = userId;
    }

    public void loginSuccessful(Instant when) {
        failedLoginAttempts = 0;
        lastSuccessfulLogin = when;
    }

    public void loginFailed(Instant when) {
        failedLoginAttempts++;
        lastFailedLogin = when;
    }
}
