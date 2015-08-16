package hw.tests.oauth2.utils

import java.time.Instant

import javax.transaction.Transactional

import groovy.transform.CompileStatic

import hw.oauth2.entities.User
import hw.oauth2.entities.UserRepository

@CompileStatic
@Transactional
class UserHelper {

    private final UserRepository userRepository

    UserHelper(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    User createUser(String userId, String password, String... roles) {
        User user = doCreateUser(userId, password)
        roles.each { role ->
            user.addEntry("AUTHORITY", "ROLE_" + role)
        }
        userRepository.save(user)
    }

    User createUser(String userId, String password, Closure closure) {
        User user = doCreateUser(userId, password)
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure.delegate = user
        closure.call()
        userRepository.save(user)
    }

    User createUser(String userId, String password, String role, Closure closure) {
        User user = createUser(userId, password, role)
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure.delegate = user
        closure.call()
        userRepository.save(user)
    }

    private User doCreateUser(String userId, String password) {
        User user = new User(userId: userId, password: password, passwordExpiresAt: Instant.now().plusSeconds(3600))
        user.getLoginStatus()
        return user
    }

    void deleteUser(String userId) {
        User user = userRepository.findByUserId(userId)
        if (user != null) {
            userRepository.delete(user)
        }
    }
}
