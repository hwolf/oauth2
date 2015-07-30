package hw.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import hw.oauth2.authentication.MyAuthenticationProvider;
import hw.oauth2.authentication.MyAuthenticationSuccessHandler;
import hw.oauth2.authentication.approvals.ApprovalServiceImpl;
import hw.oauth2.authentication.clients.ClientServiceImpl;
import hw.oauth2.authentication.tokens.TokenServiceImpl;
import hw.oauth2.entities.AccessTokenRepository;
import hw.oauth2.entities.ApprovalRepository;
import hw.oauth2.entities.ClientRepository;
import hw.oauth2.entities.RefreshTokenRepository;
import hw.oauth2.entities.UserRepository;
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
            registry.addViewController(Urls.HOME).setViewName("home");
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
        private PasswordEncoder passwordEncoder;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(authenticationProvider());
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
            return new MyAuthenticationProvider(passwordEncoder, userRepository);
        }
    }

    @Configuration
    @Order(-10)
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    protected static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers(Urls.HOME, "/webjars/**", "/error-pages/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            /* @formatter:off */
            http
                .formLogin()
                    .loginPage(Urls.LOGIN).permitAll()
                    .successHandler(successHandler())
                    .and()
                .requestMatchers()
                    .antMatchers(Urls.LOGIN, Urls.OAUTH_AUTHORIZE, Urls.OAUTH_CONFIRM_ACCESS, //
                            Urls.CHANGE_PASSWORD, "/manage/**", "/api/**") //
                    .and()
                .authorizeRequests() //
                    .antMatchers("/manage/**").hasRole("ADMIN") //
                    .antMatchers("/api/**").hasRole("ADMIN") //
                    .antMatchers(Urls.CHANGE_PASSWORD).hasAnyRole(Roles.MUST_CHANGE_PASSWORD, Roles.AUTHENTICATED) //
                    .anyRequest().authenticated();
            /* @formatter:on */
        }

        private AuthenticationSuccessHandler successHandler() {
            MyAuthenticationSuccessHandler handler = new MyAuthenticationSuccessHandler();
            handler.addRedirect("ROLE_" + Roles.MUST_CHANGE_PASSWORD, Urls.CHANGE_PASSWORD);
            return handler;
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
