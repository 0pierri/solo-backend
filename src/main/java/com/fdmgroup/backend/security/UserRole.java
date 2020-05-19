package com.fdmgroup.backend.security;

import org.springframework.security.core.GrantedAuthority;

public class UserRole implements GrantedAuthority {

    //TODO: test that USER vs ADMIN access works
    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";

    public enum Role {
        ROLE_USER,
        ROLE_ADMIN
    }

    private final String role;
    private static final UserRole User = new UserRole(Role.ROLE_USER);
    private static final UserRole Admin = new UserRole(Role.ROLE_ADMIN);

    private UserRole(Role role) {
        this.role = role.toString();
    }

    public static UserRole of(Role role) {
        switch(role) {
            case ROLE_USER: return User;
            case ROLE_ADMIN: return Admin;
        }
        throw new IllegalArgumentException("role must be one of 'UserRole.Role.ROLE_USER' or 'UserRole.Role.ROLE_ADMIN'");
    }

    @Override
    public String getAuthority() {
        return role;
    }
}
