package oauth2.controllers

import org.passay.PasswordValidator
import org.passay.SourceRule
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors

import oauth2.controllers.ChangePasswordForm;
import oauth2.controllers.ChangePasswordFormValidator;
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