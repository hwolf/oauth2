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
package oauth2.services;

import org.passay.DigitCharacterRule;
import org.passay.LengthRule;
import org.passay.LowercaseCharacterRule;
import org.passay.PasswordValidator;
import org.passay.SourceRule;
import org.passay.SpecialCharacterRule;
import org.passay.UppercaseCharacterRule;
import org.passay.UsernameRule;
import org.passay.WhitespaceRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.ImmutableList;

import oauth2.authentication.UserAuthenticationStrategy;
import oauth2.entities.UserRepository;

@Configuration
public class ServicesConfiguration {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthenticationStrategy authenticationStrategy;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public PasswordValidator passwordValidator() {
        return new PasswordValidator(ImmutableList.of( //
                new LengthRule(8, 40), // length between 8 and 16 characters
                new UsernameRule(false, true), // password different than user id
                new SourceRule(), // new password different than old password
                new UppercaseCharacterRule(1), // at least one upper-case character
                new LowercaseCharacterRule(1), // at least one lower-case character
                new DigitCharacterRule(1), // at least one digit character
                new SpecialCharacterRule(1), // at least one symbol (special character)
                new WhitespaceRule())); // no whitespace
    }

    @Bean
    public ChangePasswordService changePasswordService() {
        return new ChangePasswordService(userRepository, authenticationStrategy, passwordEncoder);
    }
}
