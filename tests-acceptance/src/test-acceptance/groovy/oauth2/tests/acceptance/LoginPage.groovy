package oauth2.tests.acceptance

import geb.Page

public class LoginPage extends Page {

    static url = "http://localhost:9999/uaa/login"

    static at = {
        heading.text() == "Please Login"
    }

    static content = {
        heading { $("h2") }
        loginForm {  $("form[role='form']") }
        loginButton { $("button[name='login']") }

        errorMessage { $("form .alert").text() }
        noErrorMessage { $("form .alert").empty }
    }

    def login(user, password) {
        loginForm.username = user
        loginForm.password = password
        loginButton.click()
    }
}