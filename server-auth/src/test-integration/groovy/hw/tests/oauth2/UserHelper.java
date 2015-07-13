package hw.tests.oauth2;

import hw.oauth.messages.user.ChangePasswordMessage;
import hw.oauth.messages.user.CreateUserMessage;
import hw.oauth.messages.user.DeleteUserMessage;
import hw.oauth2.services.admin.UserAdministrationService;
import hw.oauth2.services.admin.UserNotFoundException;

public class UserHelper {

    private final UserAdministrationService userService;

    public UserHelper(UserAdministrationService userService) {
        this.userService = userService;
    }

    public void deleteUser(String userId) {

        try {
            userService.deleteUser(DeleteUserMessage.builder(userId).build());
        } catch (UserNotFoundException ex) {
            // It's okay, if user does not exists
        }
    }

    public void createUser(String userId, String password, String... roles) {
        userService.createUser(CreateUserMessage.builder(userId).password("").withRoles(roles).build());
        userService.changePassword(ChangePasswordMessage.builder(userId).oldPassword("").newPassword(password).build());
    }
}
