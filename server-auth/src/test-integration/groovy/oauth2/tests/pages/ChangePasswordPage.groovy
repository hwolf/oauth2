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
package oauth2.tests.pages

import geb.Page

public class ChangePasswordPage extends Page {

    static url = "/uaa/change-password"

    static at = {
        heading.text() == "Change Password"
    }

    static content = {
        heading { $("h2") }

        changePaswordForm {  $("form[role='form']") }
        changePaswordButton { $("button[name='changePassword']") }

        fieldUserId { $("form #userid").text() }

        errorMessage { $("form .alert").text() }
        noErrorMessage { $("form .alert").empty }
    }

    def changePassword(oldPassword, newPassword, newPasswordConfirm) {
        changePaswordForm.oldPassword = oldPassword
        changePaswordForm.newPassword = newPassword
        changePaswordForm.newPassword2 = newPasswordConfirm
        changePaswordButton.click()
    }
}