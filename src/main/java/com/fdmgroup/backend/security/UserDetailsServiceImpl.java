package com.fdmgroup.backend.security;

import com.fdmgroup.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(u -> new UserDetailsImpl(
                        u.getId(),
                        u.getUsername(),
                        u.getPassword(),
                        List.of(UserRole.of(u.getRole()))))
                .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
    }
}
