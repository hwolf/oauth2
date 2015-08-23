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
package oauth2.authentication.clients;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import com.google.common.collect.ImmutableSet;

public class ClientDetailsBuilder {

    private String clientId;
    private String clientSecret;
    private final ImmutableSet.Builder<String> redirectUris = ImmutableSet.builder();

    private final ImmutableSet.Builder<String> authorizedGrantTypes = ImmutableSet.builder();

    private final ImmutableSet.Builder<String> scopes = ImmutableSet.builder();
    private final ImmutableSet.Builder<String> autoApprovedScopes = ImmutableSet.builder();

    private final ImmutableSet.Builder<String> resourceIds = ImmutableSet.builder();
    private final ImmutableSet.Builder<GrantedAuthority> authorities = ImmutableSet.builder();

    private Integer accessTokenValiditySeconds;
    private Integer refreshTokenValiditySeconds;

    public String clientId() {
        return clientId;
    }

    public ClientDetailsBuilder clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public ClientDetailsBuilder clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public ClientDetailsBuilder withRedirectUri(String redirectUri) {
        redirectUris.add(redirectUri);
        return this;
    }

    public ClientDetailsBuilder withAuthorizedGrantType(String authorizedGrantType) {
        authorizedGrantTypes.add(authorizedGrantType);
        return this;
    }

    public ClientDetailsBuilder withScope(String scope) {
        scopes.add(scope);
        return this;
    }

    public ClientDetailsBuilder withAutoApprovedScope(String scope) {
        scopes.add(scope);
        autoApprovedScopes.add(scope);
        return this;
    }

    public ClientDetailsBuilder withRole(String role) {
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return this;
    }

    public ClientDetailsBuilder withAuthority(String authority) {
        authorities.add(new SimpleGrantedAuthority(authority));
        return this;
    }

    public ClientDetailsBuilder withResourceId(String resourceId) {
        resourceIds.add(resourceId);
        return this;
    }

    public ClientDetailsBuilder accessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        return this;
    }

    public ClientDetailsBuilder refreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
        return this;
    }

    public ClientDetails build() {
        BaseClientDetails client = new BaseClientDetails();
        client.setClientId(clientId);
        client.setClientSecret(clientSecret);
        client.setRegisteredRedirectUri(redirectUris.build());

        client.setAuthorizedGrantTypes(authorizedGrantTypes.build());

        client.setScope(scopes.build());
        client.setAutoApproveScopes(autoApprovedScopes.build());

        client.setResourceIds(resourceIds.build());
        client.setAuthorities(authorities.build());

        client.setAccessTokenValiditySeconds(accessTokenValiditySeconds);
        client.setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
        return client;
    }
}
