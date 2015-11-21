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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "t_refresh_tokens")
@Getter
@Setter
public class RefreshToken {

    @Id
    private String tokenId;

    @Lob
    private byte[] token;

    @Lob
    private byte[] authentication;

    public OAuth2RefreshToken getToken() {
        return SerializationUtils.deserialize(token);
    }

    public void setToken(OAuth2RefreshToken token) {
        this.token = SerializationUtils.serialize(token);
    }

    public OAuth2Authentication getAuthentication() {
        return SerializationUtils.deserialize(authentication);
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = SerializationUtils.serialize(authentication);
    }
}
