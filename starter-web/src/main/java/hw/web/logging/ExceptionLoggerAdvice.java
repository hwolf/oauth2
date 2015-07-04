package hw.web.logging;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * A component which logs errors/exceptions a controller had thrown.
 */
@ControllerAdvice
public class ExceptionLoggerAdvice {

    @ExceptionHandler
    public void logException(HttpServletRequest request, HttpServletResponse response, Throwable ex)
            throws IOException {
        String url = extractUrlFromRequest(request);
        logException(url, ex);
        sendStatus(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String extractUrlFromRequest(HttpServletRequest request) {
        String url = request.getRequestURI();
        String query = request.getQueryString();
        if (StringUtils.hasText(query)) {
            url += "?" + request.getQueryString();
        }
        return url;
    }

    protected void logException(String url, Throwable ex) {
        LoggerFactory.getLogger(getClass()).error("An error occurred while invokeing URL" + url, ex);
    }

    protected void sendStatus(HttpServletResponse response, HttpStatus status) throws IOException {
        response.sendError(status.value(), status.getReasonPhrase());
    }
}
