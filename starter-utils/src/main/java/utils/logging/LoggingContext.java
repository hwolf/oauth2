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
package utils.logging;

import java.util.UUID;

import org.slf4j.MDC;

import com.google.common.annotations.VisibleForTesting;

public class LoggingContext {

    @VisibleForTesting
    static final String MDC_KEY = "context";

    public static void setContextId() {
        MDC.put(MDC_KEY, UUID.randomUUID().toString());
    }

    public static void removeContextId() {
        MDC.remove(MDC_KEY);
    }

    public static String getContextId() {
        return MDC.get(MDC_KEY);
    }
}
