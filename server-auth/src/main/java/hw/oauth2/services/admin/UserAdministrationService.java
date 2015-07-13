package hw.oauth2.services.admin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import hw.oauth.messages.UpdateAction;
import hw.oauth.messages.user.ChangePasswordMessage;
import hw.oauth.messages.user.CreateUserMessage;
import hw.oauth.messages.user.DeleteUserMessage;
import hw.oauth.messages.user.UpdateUserMessage;
import hw.oauth.messages.user.UpdateUserMessage.Visitor;
import hw.oauth2.entities.User;
import hw.oauth2.entities.UserRepository;

@Transactional
public class UserAdministrationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserAdministrationService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public void createUser(CreateUserMessage message) {
        checkUserDoesNotExist(message.getUserId());

        final User user = new User();
        user.setUserId(message.getUserId());
        user.setPassword(passwordEncoder.encode(message.getPassword()));
        user.setPasswordExpiresAt(calculatePasswordExpiresDate(0));
        user.setLoginStatus();
        message.getAuthorities().stream().forEach(authority -> user.addEntry("AUTHORITY", authority));
        userRepository.save(user);
    }

    public void deleteUser(DeleteUserMessage message) {
        User user = checkUserExists(message.getUserId());

        userRepository.delete(user);
    }

    public void updateUser(UpdateUserMessage message) {
        final User user = checkUserExists(message.getUserId());
        message.visitChanges(new Visitor() {

            @Override
            public void visitAuthority(UpdateAction action, String authority) {
                switch (action) {
                case ADD:
                    user.addEntry("AUTHORITY", authority);
                    break;
                case REMOVE:
                    user.removeEntry("AUTHORITY", authority);
                    break;
                default:
                    throw new IllegalArgumentException("UpdateAction " + action + " not supported");
                }
            }
        });
    }

    public void changePassword(ChangePasswordMessage message) {
        final User user = checkUserExists(message.getUserId());

        // Todo: Check user id and old password

        user.setPassword(passwordEncoder.encode(message.getNewPassword()));
        user.setPasswordExpiresAt(calculatePasswordExpiresDate(90));
    }

    private void checkUserDoesNotExist(String userId) {
        User user = userRepository.findByUserId(userId);
        if (user != null) {
            throw new UserAlreadyExistsException("User " + userId + " already exists");
        }
    }

    private User checkUserExists(String userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new UserNotFoundException("User " + userId + " does not exists");
        }
        return user;
    }

    private Instant calculatePasswordExpiresDate(int days) {
        return Instant.now().truncatedTo(ChronoUnit.DAYS).plus(days, ChronoUnit.DAYS);
    }
}
