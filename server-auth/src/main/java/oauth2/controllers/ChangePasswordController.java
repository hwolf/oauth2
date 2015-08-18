package oauth2.controllers;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.passay.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import oauth2.Urls;
import oauth2.services.ChangePasswordException;
import oauth2.services.ChangePasswordService;

@Controller
@RequestMapping(Urls.CHANGE_PASSWORD)
public class ChangePasswordController {

    static final String FORM_ATTRIBUTE_NAME = "changePassword";

    private final PasswordValidator passwordValidator;
    private final ChangePasswordService changePasswordService;

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Autowired
    public ChangePasswordController(PasswordValidator passwordValidator, ChangePasswordService changePasswordService) {
        this.passwordValidator = passwordValidator;
        this.changePasswordService = changePasswordService;
    }

    @InitBinder
    public void initBinder(DataBinder binder) {
        binder.addValidators(new ChangePasswordFormValidator(passwordValidator));
    }

    @ModelAttribute(FORM_ATTRIBUTE_NAME)
    public Object backingBean(Principal principal) {
        return new ChangePasswordForm(principal.getName());
    }

    @RequestMapping(method = RequestMethod.GET)
    public String setup() {
        return "change-password";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submit(HttpServletRequest request, HttpServletResponse response,
            @Validated @ModelAttribute(FORM_ATTRIBUTE_NAME) ChangePasswordForm form, BindingResult result) {

        if (result.hasErrors()) {
            return "change-password";
        }
        try {
            changePasswordService.changePassword(form.getUserId(), form.getOldPassword(), form.getNewPassword());
        } catch (ChangePasswordException ex) {
            result.rejectValue("userId", ex.getMessage(), ex.getLocalizedMessage());
        } catch (AuthenticationException ex) {
            result.reject(ex.getMessage(), ex.getLocalizedMessage());
        }
        if (result.hasErrors()) {
            form.clearPasswords();
            return "change-password";
        }
        return "redirect:" + getTargetUrl(request, response);
    }

    private String getTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest == null) {
            return "/";
        }
        return savedRequest.getRedirectUrl();
    }
}
