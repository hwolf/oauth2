package oauth2.tests.pages

import geb.Page

public class TestPage extends Page {

    static url = "/uaa/api/test"

    static at = {
        heading.text() == "Testpage"
    }

    static content = {
        heading { $("h2") }
    }
}