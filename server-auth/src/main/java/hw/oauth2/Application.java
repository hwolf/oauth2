package hw.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import hw.oauth2.approvals.ApprovalsServiceImpl;
import hw.oauth2.authentication.AuthenticationListener;
import hw.oauth2.authentication.ClientServiceImpl;
import hw.oauth2.authentication.MyPasswordEncoder;
import hw.oauth2.authentication.UserService;
import hw.oauth2.authentication.UserServiceImpl;
import hw.web.ApplicationBase;

@EnableResourceServer
public class Application extends ApplicationBase {

    @Configuration
    protected static class AuthenticationManagerConfiguration extends GlobalAuthenticationConfigurerAdapter {

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return MyPasswordEncoder.builder().defaultEncoder("bcrypt-1", new BCryptPasswordEncoder(10)).build();
        }

        @Bean
        public UserService userDetailsService() {
            return new UserServiceImpl(jdbcTemplate);
        }

        @Bean
        public AuthenticationListener authenticationListener(UserService userService) {
            return new AuthenticationListener(userService);
        }
    }

    @Configuration
    protected static class WebConfiguration extends WebMvcConfigurerAdapter {

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("home");
            registry.addViewController(Urls.LOGIN).setViewName("login");
            registry.addViewController(Urls.OAUTH_CONFIRM_ACCESS).setViewName("oauth-confirm-access");
            registry.addViewController(Urls.OAUTH_ERROR).setViewName("oauth-error");
        }
    }

    @Configuration
    @Order(-10)
    protected static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/", "/webjars/**", "/error-pages/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            /* @formatter:off */
            http
                .formLogin()
                    .loginPage(Urls.LOGIN).permitAll()
                    .and()
                .requestMatchers()
                    .antMatchers(Urls.LOGIN, Urls.OAUTH_AUTHORIZE, Urls.OAUTH_CONFIRM_ACCESS, "/manage/**") //
                    .and()
                .authorizeRequests() //
                    .antMatchers("/manage/**").hasRole("ADMIN") //
                    .anyRequest().authenticated()
                    .and();
            /* @formatter:on */
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class OAuth2Configuration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Autowired
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authenticationManager);
            endpoints.approvalStore(approvalStore());
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(clientDetailsService());
        }

        @Bean
        public ClientDetailsService clientDetailsService() {
            return new ClientServiceImpl(jdbcTemplate);
        }

        @Bean
        public ApprovalStore approvalStore() {
            return new ApprovalsServiceImpl(jdbcTemplate);
        }
    }

    public static void main(String[] args) {
        runApplication(Application.class, args);
    }
}
