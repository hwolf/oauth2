package hw.oauth2.authentication.users;

import java.util.function.Consumer;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import hw.oauth2.entities.Entry;
import hw.oauth2.entities.User;
import hw.oauth2.entities.UserRepository;

@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private enum EntryMappers {
        ROLE {

            @Override
            void setValue(String value, UserDetailsBuilder user) {
                user.withRole(value);
            }
        },

        AUTHORITY {

            @Override
            void setValue(String value, UserDetailsBuilder user) {
                user.withAuthority(value);
            }
        },

        IGNORE {

            @Override
            void setValue(String value, UserDetailsBuilder user) {
                // Ignore call
            }
        };

        abstract void setValue(String value, UserDetailsBuilder user);
    }

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findOne(userId.toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException("User " + userId + " not found");
        }
        UserDetailsBuilder builder = new UserDetailsBuilder() //
                .userId(user.getUserId()) //
                .password(user.getPassword()) //
                .passwordExpiresAt(user.getPasswordExpiresAt()) //
                .failedLogins(user.getLoginStatus().getFailedLoginAttempts());
        user.getEntries().stream().forEach(mapEntry(builder));
        return builder.build();
    }

    private Consumer<? super Entry> mapEntry(UserDetailsBuilder builder) {
        return entry -> findMapper(entry.getName()).setValue(entry.getData(), builder);
    }

    private EntryMappers findMapper(String name) {
        try {
            return EntryMappers.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return EntryMappers.IGNORE;
        }
    }
}
