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

public class HomePage extends Page {

    static url = "http://localhost:8080/"

    static at = {
        waitFor { heading.text() == "Greeting" }
    }

    static content = {
        heading { $("h1") }

        home { $("a", text: 'home') }
        login { $("a", text: 'login') }
    }
}