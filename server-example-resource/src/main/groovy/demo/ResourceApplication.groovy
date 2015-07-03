package demo

import java.security.Principal

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
@EnableOAuth2Resource
class ResourceApplication {

    @RequestMapping('/')
    def home(Principal user) {
        [id: UUID.randomUUID().toString(), name: user.name, content: 'Hello World']
    }

    static void main(String[] args) {
        SpringApplication.run ResourceApplication, args
    }
}
