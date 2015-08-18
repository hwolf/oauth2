package oauth2.controllers;

import static org.springframework.validation.ValidationUtils.rejectIfEmpty;

import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

public class ChangePasswordFormValidator implements Validator {

    private final PasswordValidator passwordValidator;

    public ChangePasswordFormValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ChangePasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        doValidate((ChangePasswordForm) target, errors);
    }

    protected void doValidate(ChangePasswordForm target, Errors errors) {
        rejectIfEmpty(errors, "userId", "requiredUserId", "Please enter your user id");
        rejectIfEmpty(errors, "oldPassword", "requiredOldPassword", "Please enter your old password");
        rejectIfEmpty(errors, "newPassword", "requiredNewPassword", "Please enter your new password");
        rejectIfEmpty(errors, "newPassword2", "requiredNewPassword2", "Please confirm your new password");

        if (!errors.hasErrors()) {
            validatePasswordConfirmation(target, errors);
        }
        if (!errors.hasErrors()) {
            validateNewPassword(target, errors);
        }
    }

    private void validatePasswordConfirmation(ChangePasswordForm target, Errors errors) {
        if (!Objects.equal(target.getNewPassword(), target.getNewPassword2())) {
            errors.rejectValue("newPassword2", "confirmedNewPasswordInvalid",
                    "Password doesn't match the confirmation");
        }
    }

    private void validateNewPassword(ChangePasswordForm target, Errors errors) {
        PasswordData passwordData = mapFormToPasswordData(target);
        RuleResult result = passwordValidator.validate(passwordData);
        if (!result.isValid()) {
            passwordValidator.getMessages(result).stream().forEach(message -> errors.reject(message, message));
        }
    }

    private PasswordData mapFormToPasswordData(ChangePasswordForm target) {
        PasswordData passwordData = new PasswordData(target.getNewPassword());
        passwordData.setUsername(target.getUserId());
        passwordData.setPasswordReferences(
                ImmutableList.of(new PasswordData.SourceReference("old", target.getOldPassword())));
        return passwordData;
    }
}
