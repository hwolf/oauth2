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