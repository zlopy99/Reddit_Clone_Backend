package com.app.reddit.service;

import com.app.reddit.model.User;
import com.app.reddit.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepo.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("No user by the name " + username));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.isEnabled(),
                true, true, true, getAuthorities("USER"));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role){
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
