package oauth2.endpoints;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import oauth2.Urls;

@RestController
public class UserDetailsEndpoint {

    static final class User {

        private final String id;
        private final String name;

        public User(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    @RequestMapping(Urls.USER)
    public User userDetails(Authentication user) {
        return new User(user.getName(), user.getName());
    }
}
