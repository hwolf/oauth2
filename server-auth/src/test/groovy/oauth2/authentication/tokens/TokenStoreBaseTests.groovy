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
package oauth2.authentication.tokens

import org.junit.Test
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.security.oauth2.provider.token.TokenStore

/**
 * Copied from {@link https://github.com/spring-projects/spring-security-oauth/blob/master/spring-security-oauth2/src/test/java/org/springframework/security/oauth2/provider/token/store/TokenStoreBaseTests.java}.
 */
abstract class TokenStoreBaseTests {

    abstract TokenStore getTokenStore()

    @Test
    void testReadingAuthenticationForTokenThatDoesNotExist() {
        assert tokenStore.readAuthentication("tokenThatDoesNotExist") == null
    }

    @Test
    void testStoreAccessToken() {
        OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request("id", false), new TestAuthentication("test2", false))
        OAuth2AccessToken expectedOAuth2AccessToken = new DefaultOAuth2AccessToken("testToken")
        tokenStore.storeAccessToken(expectedOAuth2AccessToken, expectedAuthentication)

        OAuth2AccessToken actualOAuth2AccessToken = tokenStore.readAccessToken("testToken")
        assert expectedOAuth2AccessToken == actualOAuth2AccessToken
        assert expectedAuthentication == tokenStore.readAuthentication(expectedOAuth2AccessToken)

        tokenStore.removeAccessToken(expectedOAuth2AccessToken)
        assert tokenStore.readAccessToken("testToken") == null
        assert tokenStore.readAuthentication(expectedOAuth2AccessToken.value) == null
    }

    @Test
    void testStoreAccessTokenTwice() {
        OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request( "id", false), new TestAuthentication("test2", false))
        OAuth2AccessToken expectedOAuth2AccessToken = new DefaultOAuth2AccessToken("testToken")
        tokenStore.storeAccessToken(expectedOAuth2AccessToken, expectedAuthentication)
        tokenStore.storeAccessToken(expectedOAuth2AccessToken, expectedAuthentication)

        OAuth2AccessToken actualOAuth2AccessToken = tokenStore.readAccessToken("testToken")
        assert expectedOAuth2AccessToken ==  actualOAuth2AccessToken
        assert expectedAuthentication ==  tokenStore.readAuthentication(expectedOAuth2AccessToken)

        tokenStore.removeAccessToken(expectedOAuth2AccessToken)
        assert tokenStore.readAccessToken("testToken") == null
        assert tokenStore.readAuthentication(expectedOAuth2AccessToken.value) == null
    }

    @Test
    void testRetrieveAccessToken() {
        //Test approved request
        OAuth2Request storedOAuth2Request = RequestTokenFactory.createOAuth2Request("id", true)
        OAuth2Authentication authentication = new OAuth2Authentication(storedOAuth2Request, new TestAuthentication("test2", true))
        DefaultOAuth2AccessToken expectedOAuth2AccessToken = new DefaultOAuth2AccessToken("testToken")
        expectedOAuth2AccessToken.setExpiration(new Date(Long.MAX_VALUE-1))
        tokenStore.storeAccessToken(expectedOAuth2AccessToken, authentication)

        //Test unapproved request
        storedOAuth2Request = RequestTokenFactory.createOAuth2Request("id", false)
        authentication = new OAuth2Authentication(storedOAuth2Request, new TestAuthentication("test2", true))
        OAuth2AccessToken actualOAuth2AccessToken = tokenStore.getAccessToken(authentication)
        assert expectedOAuth2AccessToken == actualOAuth2AccessToken
        assert authentication.userAuthentication == tokenStore.readAuthentication(expectedOAuth2AccessToken.value).userAuthentication

        // The authorizationRequest does not match because it is unapproved, but the token was granted to an approved request
        assert storedOAuth2Request != tokenStore.readAuthentication(expectedOAuth2AccessToken.value).getOAuth2Request()

        actualOAuth2AccessToken = tokenStore.getAccessToken(authentication)
        assert expectedOAuth2AccessToken == actualOAuth2AccessToken

        tokenStore.removeAccessToken(expectedOAuth2AccessToken)
        assert tokenStore.readAccessToken("testToken") == null
        assert tokenStore.readAuthentication(expectedOAuth2AccessToken.value) == null
        assert tokenStore.getAccessToken(authentication) == null
    }

    @Test
    void testFindAccessTokensByClientIdAndUserName() {
        OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request("id", false), new TestAuthentication("test2", false))
        OAuth2AccessToken expectedOAuth2AccessToken = new DefaultOAuth2AccessToken("testToken")
        tokenStore.storeAccessToken(expectedOAuth2AccessToken, expectedAuthentication)

        Collection<OAuth2AccessToken> actualOAuth2AccessTokens = tokenStore.findTokensByClientIdAndUserName("id", "test2")
        assert actualOAuth2AccessTokens.size() == 1
    }

    @Test
    void testFindAccessTokensByClientId() {
        OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request("id", false), new TestAuthentication("test2", false))
        OAuth2AccessToken expectedOAuth2AccessToken = new DefaultOAuth2AccessToken("testToken")
        tokenStore.storeAccessToken(expectedOAuth2AccessToken, expectedAuthentication)

        Collection<OAuth2AccessToken> actualOAuth2AccessTokens = tokenStore.findTokensByClientId("id")
        assert actualOAuth2AccessTokens.size() == 1
    }

    @Test
    void testReadingAccessTokenForTokenThatDoesNotExist() {
        assert tokenStore.readAccessToken("tokenThatDoesNotExist") == null
    }

    @Test
    public void testRefreshTokenIsNotStoredDuringAccessToken() {
        OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request("id", false), new TestAuthentication("test2", false))
        DefaultOAuth2AccessToken expectedOAuth2AccessToken = new DefaultOAuth2AccessToken("testToken")
        expectedOAuth2AccessToken.setRefreshToken(new DefaultOAuth2RefreshToken("refreshToken"))
        tokenStore.storeAccessToken(expectedOAuth2AccessToken, expectedAuthentication)

        OAuth2AccessToken actualOAuth2AccessToken = tokenStore.readAccessToken("testToken")
        assert actualOAuth2AccessToken.refreshToken != null

        assert tokenStore.readRefreshToken("refreshToken") == null
    }

    /**
     * NB: This used to test expiring refresh tokens. That test has been moved to sub-classes since not all stores support the functionality
     */
    @Test
    void testStoreRefreshToken() {
        DefaultOAuth2RefreshToken expectedRefreshToken = new DefaultOAuth2RefreshToken("testToken")
        OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request("id", false), new TestAuthentication("test2", false))
        tokenStore.storeRefreshToken(expectedRefreshToken, expectedAuthentication)

        OAuth2RefreshToken actualExpiringRefreshToken = tokenStore.readRefreshToken("testToken")
        assert expectedRefreshToken == actualExpiringRefreshToken
        assert expectedAuthentication == tokenStore.readAuthenticationForRefreshToken(expectedRefreshToken)

        tokenStore.removeRefreshToken(expectedRefreshToken)
        assert tokenStore.readRefreshToken("testToken") == null
        assert tokenStore.readAuthentication(expectedRefreshToken.value) == null
    }

    @Test
    void testReadingRefreshTokenForTokenThatDoesNotExist() {
        tokenStore.readRefreshToken("tokenThatDoesNotExist")
    }

    @Test
    void testGetAccessTokenForDeletedUser() throws Exception {
        //Test approved request
        OAuth2Request storedOAuth2Request = RequestTokenFactory.createOAuth2Request("id", true)
        OAuth2Authentication expectedAuthentication = new OAuth2Authentication(storedOAuth2Request, new TestAuthentication("test", true))
        OAuth2AccessToken expectedOAuth2AccessToken = new DefaultOAuth2AccessToken("testToken")
        tokenStore.storeAccessToken(expectedOAuth2AccessToken, expectedAuthentication)
        assert expectedOAuth2AccessToken == tokenStore.getAccessToken(expectedAuthentication)
        assert expectedAuthentication == tokenStore.readAuthentication(expectedOAuth2AccessToken.value)

        //Test unapproved request
        storedOAuth2Request = RequestTokenFactory.createOAuth2Request("id", false)
        OAuth2Authentication anotherAuthentication = new OAuth2Authentication(storedOAuth2Request, new TestAuthentication("test", true))
        assert expectedOAuth2AccessToken == tokenStore.getAccessToken(anotherAuthentication)

        // The generated key for the authentication is the same as before, but the two auths are not equal. This could
        // happen if there are 2 users in a system with the same username, or (more likely), if a user account was
        // deleted and re-created.
        assert anotherAuthentication.userAuthentication == tokenStore.readAuthentication(expectedOAuth2AccessToken.value).userAuthentication
        // The authorizationRequest does not match because it is unapproved, but the token was granted to an approved request
        assert storedOAuth2Request != tokenStore.readAuthentication(expectedOAuth2AccessToken.value).getOAuth2Request()
    }

    @Test
    void testRemoveRefreshToken() {
        OAuth2RefreshToken expectedExpiringRefreshToken = new DefaultExpiringOAuth2RefreshToken("testToken",
                new Date())
        OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request("id", false), new TestAuthentication("test2", false))
        tokenStore.storeRefreshToken(expectedExpiringRefreshToken, expectedAuthentication)
        tokenStore.removeRefreshToken(expectedExpiringRefreshToken)

        assert tokenStore.readRefreshToken("testToken") == null
    }

    @Test
    void testRemovedTokenCannotBeFoundByUsername() {
        OAuth2AccessToken token = new DefaultOAuth2AccessToken("testToken")
        OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request(
                "id", false), new TestAuthentication("test2", false))
        tokenStore.storeAccessToken(token, expectedAuthentication)
        tokenStore.removeAccessToken(token)
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName("id", "test2")
        assert !tokens.contains(token)
        assert tokens.isEmpty()
    }

    protected static class TestAuthentication extends AbstractAuthenticationToken {

        private static final long serialVersionUID = 1L
        private String principal

        TestAuthentication(String name, boolean authenticated) {
            super(null)
            setAuthenticated(authenticated)
            this.principal = name
        }

        Object getCredentials() {
            return null
        }

        Object getPrincipal() {
            return this.principal
        }
    }
}