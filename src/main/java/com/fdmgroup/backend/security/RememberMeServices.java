package com.fdmgroup.backend.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class RememberMeServices extends PersistentTokenBasedRememberMeServices {

    public RememberMeServices(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
    }

    @Override
    protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
        //Note: Ignores AbstractRememberMeServices#alwaysRemember flag (assumes false)
        return Optional.ofNullable(request.getHeader(getParameter()))
                .map(h -> h.equals("true"))
                .orElse(false);
    }
}
