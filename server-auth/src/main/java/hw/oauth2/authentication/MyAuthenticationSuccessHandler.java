package hw.oauth2.authentication;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final Map<String, String> redirectsByAuthority;

    public MyAuthenticationSuccessHandler(Map<String, String> redirectsByAuthority) {
        this.redirectsByAuthority = ImmutableMap.copyOf(redirectsByAuthority);
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
