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
package oauth2.controllers

import org.passay.PasswordValidator
import org.passay.SourceRule
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors

import spock.lang.Specification

class ChangePasswordFormValidatorSpec extends Specification {

    ChangePasswordFormValidator validator = new ChangePasswordFormValidator(new PasswordValidator([new SourceRule()]))

    def "Should only support ChangePasswordForm"() {

        expect:
        validator.supports(ChangePasswordForm)

        !validator.supports(String)
    }

    def "If ChangePasswordForm is valid, no error should reported"() {

        given:
        ChangePasswordForm form = createValidChangePasswordForm()
        Errors errors = new BeanPropertyBindingResult(form, "form")

        when:
        validator.validate(form, errors)

        then:
        !errors.hasErrors()
    }

    def "If user id is missing, an error should reported"() {

        given:
        ChangePasswordForm form = createChangePasswordForm("")
        Errors errors = new BeanPropertyBindingResult(form, "form")

        when:
        validator.validate(form, errors)

        then:
        errors.allErrors.field == ["userId"]
    }

    def "If old password is missing, an error should reported"() {

        given:
        ChangePasswordForm form = createValidChangePasswordForm()
        Errors errors = new BeanPropertyBindingResult(form, "form")

        when:
        form.oldPassword = ""
        validator.validate(form, errors)

        then:
        errors.allErrors.field == ["oldPassword"]
    }

    def "If new password is missing, an error should reported"() {

        given:
        ChangePasswordForm form = createValidChangePasswordForm()
        Errors errors = new BeanPropertyBindingResult(form, "form")

        when:
        form.newPassword = ""
        validator.validate(form, errors)

        then:
        errors.allErrors.field == ["newPassword"]
    }

    def "If confirmed new password is missing, an error should reported"() {

        given:
        ChangePasswordForm form = createValidChangePasswordForm()
        Errors errors = new BeanPropertyBindingResult(form, "form")

        when:
        form.newPassword2 = ""
        validator.validate(form, errors)

        then:
        errors.allErrors.field == ["newPassword2"]
    }

    def "If confirmed new password differs from new password, an error should reported"() {

        given:
        ChangePasswordForm form = createValidChangePasswordForm()
        Errors errors = new BeanPropertyBindingResult(form, "form")

        when:
        form.newPassword2 = form.newPassword + "sdsd"
        validator.validate(form, errors)

        then:
        errors.allErrors.field == ["newPassword2"]
    }

    def "If a password validator rule does not match, an error should reported"() {

        given:
        ChangePasswordForm form = createValidChangePasswordForm()
        Errors errors = new BeanPropertyBindingResult(form, "form")

        when:
        form.newPassword = form.oldPassword
        form.newPassword2 = form.oldPassword
        validator.validate(form, errors)

        then:
        errors.allErrors.objectName == ["form"]
    }

    private ChangePasswordForm createValidChangePasswordForm() {
        return createChangePasswordForm("user id")
    }

    private ChangePasswordForm createChangePasswordForm(String userId) {
        ChangePasswordForm form = new ChangePasswordForm(userId)
        form.oldPassword = "old password"
        form.newPassword = "new password"
        form.newPassword2 = "new password"
        return form
    }
}