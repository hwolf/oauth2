
driver = "firefox"

reportsDir = "build/geb-reports"

waiting { timeout = 5 }

environments {
    phantomjs { driver = "org.openqa.selenium.phantomjs.PhantomJSDriver" }
    firefox { driver = "firefox" }
    chrome { driver = "chrome" }
}
