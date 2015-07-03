package hw.tests.oauth2.pages

import geb.Page

public class LoginPage extends Page {

    static at = {
        heading.text() == "Please Login"
    }

    static content = {
        heading { $("h2") }
        loginForm {  $("form[role='form']") }
        loginButton { $("button[name='login']") }
    }

    def login(user, password) {
        loginForm.username = user
        loginForm.password = password
        loginButton.click()
    }
}