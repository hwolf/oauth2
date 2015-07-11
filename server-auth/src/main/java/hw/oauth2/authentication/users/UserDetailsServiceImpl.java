package hw.oauth2.authentication.users;

import java.util.List;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String SQL_LOAD_USERS_BY_USERID = "select " //
            + "    u.user_id, " //
            + "    u.password, " //
            + "    u.password_expired, " //
            + "    s.failed_login_attempts " //
            + "from " //
            + "    t_users u " //
            + "        join t_login_status s on s.user_id = u.user_id " //
            + "where " //
            + "    u.user_id = ?";

    private static final String SQL_LOAD_ENTRIES_BY_USERID = "select " //
            + "    name, " //
            + "    data " //
            + "from " //
            + "    t_user_entries " //
            + "where " //
            + "    user_id = ?";

    private enum EntryType {
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

    private final JdbcTemplate jdbcTemplate;
    private final MessageSourceAccessor messages;

    public UserDetailsServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        messages = SpringSecurityMessageSource.getAccessor();
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        List<UserDetailsBuilder> users = loadUsersByUsername(userId);
        if (users.isEmpty()) {
            throw new UsernameNotFoundException(
                    messages.getMessage("JdbcDaoImpl.notFound", new Object[] { userId }, "Username {0} not found"));
        }
        UserDetailsBuilder user = users.get(0);
        loadEntries(user.userId(), user);
        return user.build();
    }

    protected List<UserDetailsBuilder> loadUsersByUsername(String username) {
        return jdbcTemplate.query(SQL_LOAD_USERS_BY_USERID, new String[] { username.toLowerCase() }, (rs, rowNum) -> {
            return new UserDetailsBuilder() //
                    .userId(rs.getString(1)) //
                    .password(rs.getString(2)) //
                    .passwordExpiresAt(rs.getDate(3)) //
                    .failedLogins(rs.getInt(4));
        });
    }

    protected void loadEntries(String userId, UserDetailsBuilder user) {
        jdbcTemplate.query(SQL_LOAD_ENTRIES_BY_USERID, new String[] { userId }, rs -> {
            String name = rs.getString(1);
            String data = rs.getString(2);
            findEntry(name).setValue(data, user);
        });
    }

    private EntryType findEntry(String name) {
        try {
            return EntryType.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return EntryType.IGNORE;
        }
    }
}
