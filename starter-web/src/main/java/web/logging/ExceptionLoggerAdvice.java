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
package web.logging;

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
