package com.fdmgroup.backend.security;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.sql.DataSource;

@Log4j2
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final int ONE_YEAR = 52*7*24*60*60;

    private final UserDetailsServiceImpl userDetailsService;
    private final DataSource dataSource;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf()
                .csrfTokenRepository(csrfTokenRepository())
                .and()
            .requestCache().disable()
            .authorizeRequests()
                .antMatchers("/login", "/signup", "/csrf")
                    .permitAll()
                .anyRequest()
                    .authenticated()
                .and()
            .rememberMe()
                .rememberMeServices(rememberMeServices())
                .and()
            .addFilter(new AuthenticationFilter(authenticationManager(), rememberMeServices()))
            .exceptionHandling()
                .accessDeniedHandler((req, res, a) -> res.setStatus(403))
                .authenticationEntryPoint((req, res, a) -> res.setStatus(401))
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));
    }

    @Bean(name="passwordEncoder")
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {
        var repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repo.setCookiePath("/");
        return repo;
    }

    @Bean
    public PersistentTokenBasedRememberMeServices rememberMeServices() {
        var svc = new RememberMeServices("super secret key",
                userDetailsService,
                persistentTokenRepository());
        svc.setParameter("x-remember-me");
        svc.setCookieName("rm");
        svc.setTokenValiditySeconds(ONE_YEAR);
        return svc;
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        return new TokenRepository(dataSource);
    }
}
