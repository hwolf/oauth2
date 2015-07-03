package hw.oauth2.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import com.google.common.collect.ImmutableSet;

@SuppressWarnings("hiding")
public class ClientDetailsBuilder {

    private String clientId;
    private String clientSecret;
    private String redirectUri;

    private final ImmutableSet.Builder<String> authorizedGrantTypes = ImmutableSet.builder();

    private final ImmutableSet.Builder<String> scope = ImmutableSet.builder();
    private final ImmutableSet.Builder<String> approvedScope = ImmutableSet.builder();

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

    public ClientDetailsBuilder redirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }

    public ClientDetailsBuilder authorizedGrantTypes(String... authorizedGrantTypes) {
        this.authorizedGrantTypes.add(authorizedGrantTypes);
        return this;
    }

    public ClientDetailsBuilder scopes(String... scopes) {
        scope.add(scopes);
        return this;
    }

    public ClientDetailsBuilder approvedScopes(String scopes) {
        approvedScope.add(scopes);
        return this;
    }

    public ClientDetailsBuilder authorities(GrantedAuthority... authorities) {
        this.authorities.add(authorities);
        return this;
    }

    public ClientDetailsBuilder resourceIds(String... resourceIds) {
        this.resourceIds.add(resourceIds);
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
        client.setRegisteredRedirectUri(redirectUri == null ? null : ImmutableSet.of(redirectUri));

        client.setAuthorizedGrantTypes(authorizedGrantTypes.build());

        client.setScope(scope.build());
        client.setAutoApproveScopes(approvedScope.build());

        client.setResourceIds(resourceIds.build());
        client.setAuthorities(authorities.build());

        client.setAccessTokenValiditySeconds(accessTokenValiditySeconds);
        client.setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
        return client;
    }
}
