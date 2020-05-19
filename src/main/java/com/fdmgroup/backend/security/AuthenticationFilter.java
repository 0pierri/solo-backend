package com.fdmgroup.backend.security;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdmgroup.backend.model.UserDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter  {

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                RememberMeServices rememberMeServices) {
        setAuthenticationManager(authenticationManager);
        setRememberMeServices(rememberMeServices);

        setAuthenticationSuccessHandler((req, res, a) -> res.setStatus(HttpStatus.OK.value()));
        setAuthenticationFailureHandler((req, res, a) -> res.setStatus(HttpStatus.UNAUTHORIZED.value()));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            UserDTO dto = new ObjectMapper().readValue(request.getInputStream(), UserDTO.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getUsername().trim(),
                            dto.getPassword().trim())
            );
        } catch (JsonParseException | JsonMappingException e) {
            logger.warn("Bad request body while mapping to UserDTO");
            throw new BadCredentialsException("Bad request body");
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new AuthenticationServiceException("IO error");
        }
    }
}
