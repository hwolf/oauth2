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
package oauth2.authentication.tokens;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Request;

/**
 * Copied from
 * {@link https://github.com/spring-projects/spring-security-oauth/tree/master/spring-security-oauth2/src/test/java/org/springframework/security/oauth2/provider}
 * .
 */
public class RequestTokenFactory {

    public static OAuth2Request createOAuth2Request(Map<String, String> requestParameters, String clientId,
            Collection<? extends GrantedAuthority> authorities, boolean approved, Collection<String> scope,
            Set<String> resourceIds, String redirectUri, Set<String> responseTypes,
            Map<String, Serializable> extensionProperties) {
        return new OAuth2Request(requestParameters, clientId, authorities, approved,
                scope == null ? null : new LinkedHashSet<>(scope), resourceIds, redirectUri, responseTypes,
                extensionProperties);
    }

    public static OAuth2Request createOAuth2Request(String clientId, boolean approved) {
        return createOAuth2Request(clientId, approved, null);
    }

    public static OAuth2Request createOAuth2Request(String clientId, boolean approved, Collection<String> scope) {
        return createOAuth2Request(Collections.<String, String> emptyMap(), clientId, approved, scope);
    }

    public static OAuth2Request createOAuth2Request(Map<String, String> parameters, String clientId, boolean approved,
            Collection<String> scope) {
        return createOAuth2Request(parameters, clientId, null, approved, scope, null, null, null, null);
    }
}
