package hw.web.logging;

import hw.utils.logging.LoggingContext;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        LoggingContext.setContextId();
        try {
            filterChain.doFilter(request, response);
        } finally {
            LoggingContext.removeContextId();
        }
    }
}
