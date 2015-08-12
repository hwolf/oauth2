package hw.tests.oauth2.pages

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