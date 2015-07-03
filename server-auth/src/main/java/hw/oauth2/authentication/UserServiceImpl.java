package hw.oauth2.authentication;

import java.util.Date;
import java.util.List;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImpl implements UserService {

    private static final String SQL_LOAD_USERS_BY_USERNAME = "select username, password, password_expired, login_attempts " //
            + "from t_users " //
            + "where username = ?";

    private static final String SQL_LOAD_AUTHORITIES_BY_USERNAME = "select username, authority " //
            + "from t_user_authorities " //
            + "where username = ?";

    private static final String SQL_UPDATE_RESET_LOGIN_ATTEMPTS = "update t_users " //
            + "set login_attempts = 0, last_successfull_login = ? " //
            + "where username = ?";

    private static final String SQL_UPDATE_UPDATE_LOGIN_ATTEMPTS = "update t_users " //
            + "set login_attempts = login_attempts + 1, last_failed_login = ? " //
            + "where username = ?";

    private final JdbcTemplate jdbc;
    private final MessageSourceAccessor messages;

    public UserServiceImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        messages = SpringSecurityMessageSource.getAccessor();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsBuilder> users = loadUsersByUsername(username);
        if (users.isEmpty()) {
            throw new UsernameNotFoundException(
                    messages.getMessage("JdbcDaoImpl.notFound", new Object[] { username }, "Username {0} not found"));
        }
        UserDetailsBuilder user = users.get(0);
        loadUserAuthorities(user.username(), user);
        return user.build();
    }

    protected List<UserDetailsBuilder> loadUsersByUsername(String username) {
        return jdbc.query(SQL_LOAD_USERS_BY_USERNAME, new String[] { username.toLowerCase() }, (rs, rowNum) -> {
            return new UserDetailsBuilder() //
                    .enabled(true) //
                    .accountNonExpired(true) //
                    .username(rs.getString(1)) //
                    .password(rs.getString(2)) //
                    .credentialsNonExpired(rs.getDate(3).after(new Date())) //
                    .accountNonLocked(rs.getInt(4) <= 3);
        });
    }

    protected void loadUserAuthorities(String username, UserDetailsBuilder user) {
        jdbc.query(SQL_LOAD_AUTHORITIES_BY_USERNAME, new String[] { username.toLowerCase() }, rs -> {
            user.authorities(new SimpleGrantedAuthority(rs.getString(2)));
        });
    }

    @Override
    public void updateFailAttempts(String username) {
        jdbc.update(SQL_UPDATE_UPDATE_LOGIN_ATTEMPTS, new Date(), username.toLowerCase());
    }

    @Override
    public void resetFailAttempts(String username) {
        jdbc.update(SQL_UPDATE_RESET_LOGIN_ATTEMPTS, new Date(), username.toLowerCase());
    }
}
