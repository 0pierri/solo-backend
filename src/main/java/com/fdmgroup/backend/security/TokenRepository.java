package com.fdmgroup.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

// Store remember-me tokens in the application database
@Component
public class TokenRepository extends JdbcTokenRepositoryImpl {

    @Autowired
    public TokenRepository(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        super.createNewToken(token);
    }
}
