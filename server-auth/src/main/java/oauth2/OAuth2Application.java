/*
 * Copyright 2015 H. Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
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

import oauth2.authentication.DefaultUserAuthenticationStrategy;
import oauth2.authentication.MyAuthenticationSuccessHandler;
import oauth2.authentication.UserAuthenticationProvider;
import oauth2.authentication.UserAuthenticationStrategy;
import oauth2.authentication.approvals.ApprovalServiceImpl;
import oauth2.authentication.clients.ClientServiceImpl;
import oauth2.authentication.tokens.TokenServiceImpl;
import oauth2.entities.AccessTokenRepository;
import oauth2.entities.ApprovalRepository;
import oauth2.entities.ClientRepository;
import oauth2.entities.RefreshTokenRepository;
import oauth2.entities.UserRepository;
import oauth2.password.MyPasswordEncoder;
import web.ApplicationBase;

@EnableResourceServer
public class OAuth2Application extends ApplicationBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Application.class);

    /**
     * Log database URL to verify which database we use.
     */
    @Autowired(required = false)
    public void setDataSourceProperties(DataSourceProperties properties) {
        LOGGER.info("Use database {}", properties.getUrl());
    }

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
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public static class ErrorPagesCustomizer implements EmbeddedServletContainerCustomizer {

        @Override
        public void customize(ConfigurableEmbeddedServletContainer container) {
            container.addErrorPages(new ErrorPage(HttpStatus.UNAUTHORIZED, "/error-pages/401.html"));
            container.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/error-pages/403.html"));
            container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error-pages/404.html"));
            container.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-pages/500.html"));
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
        public UserAuthenticationStrategy authenticationStrategy() {
            return new DefaultUserAuthenticationStrategy(passwordEncoder);
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
            return new UserAuthenticationProvider(userRepository, authenticationStrategy());
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
        runApplication(OAuth2Application.class, args);
    }
}
