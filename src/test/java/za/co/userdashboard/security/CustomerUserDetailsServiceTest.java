package za.co.userdashboard.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import za.co.userdashboard.entity.AppUser;
import za.co.userdashboard.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomerUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_whenUserExists_returnsUserDetails() {
        AppUser appUser = new AppUser();
        appUser.setId(1L);
        appUser.setUserName("john@example.com");
        appUser.setPassword("$2a$12$hashedpassword");

        when(userRepository.findByUserName("john@example.com")).thenReturn(Optional.of(appUser));

        UserDetails details = userDetailsService.loadUserByUsername("john@example.com");

        assertNotNull(details);
        assertEquals("john@example.com", details.getUsername());
        assertEquals("$2a$12$hashedpassword", details.getPassword());
        assertTrue(details.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_whenUserNotFound_throwsUsernameNotFoundException() {
        when(userRepository.findByUserName("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown@example.com"));
    }
}
