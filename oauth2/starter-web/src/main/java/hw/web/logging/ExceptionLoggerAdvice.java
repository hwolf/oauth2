package hw.web.logging;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * A component which logs errors/exceptions a controller had thrown.
 */
@ControllerAdvice
public class ExceptionLoggerAdvice {

    @ExceptionHandler
    public void logException(HttpServletRequest request, HttpServletResponse response, Throwable ex) throws IOException {
        LoggerFactory.getLogger(getClass()).error("An error occurred while invokeing URL" + getUrlFromRequest(request),
                ex);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Pla Pla");
    }

    private String getUrlFromRequest(HttpServletRequest request) {
        String url = request.getRequestURI();
        String query = request.getQueryString();
        if (StringUtils.hasText(query)) {
            url += "?" + request.getQueryString();
        }
        return url;
    }
}
