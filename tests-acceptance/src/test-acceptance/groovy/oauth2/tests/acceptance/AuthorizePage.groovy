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
package oauth2.tests.acceptance

import geb.Page

public class AuthorizePage extends Page {

    static url = "http://localhost:9999/uaa/oauth/authorize"

    static at = {
        heading.text() == "Please confirm"
    }

    static content = {
        heading { $("h2") }
        authorizeForm {  $("form[role='form']") }
        authorizeButton { $("button[name='authorize']") }
    }

    def authorize(scopes) {
        authorizeForm['scope.scope1'] = (scopes.contains('scope1'))
        authorizeForm['scope.scope2'] = (scopes.contains('scope2'))
        authorizeForm['scope.scope3'] = (scopes.contains('scope3'))
        authorizeButton.click()
    }
}