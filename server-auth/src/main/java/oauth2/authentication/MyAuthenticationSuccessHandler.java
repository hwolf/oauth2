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
package oauth2.authentication;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final Map<String, String> redirectsByAuthority = Maps.newConcurrentMap();

    public void addRedirect(String authority, String targetUrl) {
        redirectsByAuthority.put(authority, targetUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {

        if (authentication.isAuthenticated()) {
            for (Map.Entry<String, String> entry : redirectsByAuthority.entrySet()) {
                if (authentication.getAuthorities().stream()
                        .anyMatch(authority -> Objects.equal(entry.getKey(), authority.getAuthority()))) {
                    getRedirectStrategy().sendRedirect(request, response, entry.getValue());
                    return;
                }
            }
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
