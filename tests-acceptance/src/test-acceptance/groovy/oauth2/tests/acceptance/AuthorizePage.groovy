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