package za.co.userdashboard.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import za.co.userdashboard.entity.AppUser;
import za.co.userdashboard.repository.UserRepository;

import java.util.Collections;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomerUserDetailsService(UserRepository userRepository){
        this.userRepository =userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AppUser appUser = userRepository.findByUserName(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(appUser.getUserName())
                .password(appUser.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
