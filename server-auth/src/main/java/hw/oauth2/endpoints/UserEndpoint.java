package hw.oauth2.endpoints;

import hw.oauth2.Urls;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserEndpoint {

    @RequestMapping(Urls.USER)
    public User userDetails(Authentication user) {
        return new User(user.getName(), user.getName());
    }
}
