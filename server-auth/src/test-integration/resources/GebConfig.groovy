
driver = "firefox"

waiting { timeout = 2 }

environments {
    phantomjs { driver = "org.openqa.selenium.phantomjs.PhantomJSDriver" }
    firefox { driver = "firefox" }
    chrome { driver = "chrome" }
}
