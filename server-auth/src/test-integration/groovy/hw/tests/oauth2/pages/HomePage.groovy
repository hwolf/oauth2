package hw.tests.oauth2.pages

import geb.Page

public class HomePage extends Page {

    static url = "/uaa"

    static at = {
        heading.text() == "Welcome"
    }

    static content = {
        heading { $("h2") }
    }
}