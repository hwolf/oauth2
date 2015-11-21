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
package oauth2;

public class Urls {

    public static final String HOME = "/";
    public static final String LOGIN = "/login";
    public static final String CHANGE_PASSWORD = "/change-password"; // NOSONAR
    public static final String USER = "/user";

    public static final String OAUTH_AUTHORIZE = "/oauth/authorize";
    public static final String OAUTH_CHECK_TOKEN = "/oauth/check_token";
    public static final String OAUTH_CONFIRM_ACCESS = "/oauth/confirm_access";
    public static final String OAUTH_ERROR = "/oauth/error";
}
