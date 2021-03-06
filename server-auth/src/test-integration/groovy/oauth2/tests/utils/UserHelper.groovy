/*
 * Copyright 2015 H. Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oauth2.tests.utils

import java.time.Instant

import javax.transaction.Transactional

import oauth2.entities.User
import oauth2.entities.UserRepository
import groovy.transform.CompileStatic

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
