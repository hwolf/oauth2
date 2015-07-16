package hw.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import hw.oauth2.authentication.approvals.ApprovalServiceImpl;
import hw.oauth2.authentication.clients.ClientServiceImpl;
import hw.oauth2.authentication.tokens.TokenServiceImpl;
import hw.oauth2.authentication.users.UpdateLoginStatus;
import hw.oauth2.authentication.users.UserDetailsServiceImpl;
import hw.oauth2.entities.AccessTokenRepository;
import hw.oauth2.entities.ApprovalRepository;
import hw.oauth2.entities.ClientRepository;
import hw.oauth2.entities.LoginStatusRepository;
import hw.oauth2.entities.RefreshTokenRepository;
import hw.oauth2.entities.user.UserRepository;
import hw.oauth2.password.MyPasswordEncoder;
import hw.web.ApplicationBase;

@EnableResourceServer
public class HwOauth2Application extends ApplicationBase {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return MyPasswordEncoder.builder().defaultEncoder("bcrypt-1", new BCryptPasswordEncoder(10)).build();
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
    protected static class AuthenticationManagerConfiguration extends GlobalAuthenticationConfigurerAdapter {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private LoginStatusRepository loginStatusRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder);
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return new UserDetailsServiceImpl(userRepository);
        }

        @Bean
        public UpdateLoginStatus authenticationListener() {
            return new UpdateLoginStatus(loginStatusRepository);
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
                    .antMatchers(Urls.LOGIN, Urls.OAUTH_AUTHORIZE, Urls.OAUTH_CONFIRM_ACCESS, "/manage/**", "/api/**") //
                    .and()
                .authorizeRequests() //
                    .antMatchers("/manage/**").hasRole("ADMIN") //
                    .antMatchers("/api/**").hasRole("ADMIN") //
                    .anyRequest().authenticated()
                    .and();
            /* @formatter:on */
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class OAuth2Configuration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private ClientRepository clientRepository;

        @Autowired
        private ApprovalRepository approvalRepository;

        @Autowired
        private AccessTokenRepository accessTokenRepository;

        @Autowired
        private RefreshTokenRepository refreshTokenRepository;

        @Autowired
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authenticationManager);
            endpoints.tokenStore(tokenStore());
            endpoints.approvalStore(approvalStore());
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(clientDetailsService());
        }

        @Bean
        public ClientDetailsService clientDetailsService() {
            return new ClientServiceImpl(clientRepository);
        }

        @Bean
        public TokenStore tokenStore() {
            return new TokenServiceImpl(accessTokenRepository, refreshTokenRepository);
        }

        @Bean
        public ApprovalStore approvalStore() {
            return new ApprovalServiceImpl(approvalRepository);
        }
    }

    public static void main(String[] args) {
        runApplication(HwOauth2Application.class, args);
    }
}
