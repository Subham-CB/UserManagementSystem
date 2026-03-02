package za.co.userdashboard.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import za.co.userdashboard.dto.UserCreationDTO;
import za.co.userdashboard.dto.UserResponseDTO;
import za.co.userdashboard.entity.AppUser;
import za.co.userdashboard.exception.UserAlreadyExistsException;
import za.co.userdashboard.repository.UserRepository;
import za.co.userdashboard.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    // ── createUser ─────────────────────────────────────────────────────────────

    @Test
    void createUser_success_returnsUserResponseDTO() {
        UserCreationDTO dto = buildCreationDTO("john@example.com", "John", "Doe", "secret123");

        AppUser appUser = buildAppUser(1L, "john@example.com", "John", "Doe", "hashed");
        UserResponseDTO expected = buildResponseDTO("john@example.com", "John", "Doe");

        when(userRepository.findByUserName("john@example.com")).thenReturn(Optional.empty());
        when(modelMapper.map(dto, AppUser.class)).thenReturn(appUser);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(AppUser.class))).thenReturn(appUser);
        when(modelMapper.map(appUser, UserResponseDTO.class)).thenReturn(expected);

        UserResponseDTO actual = userService.createUser(dto);

        assertNotNull(actual);
        assertEquals("John", actual.getFirstName());
        assertEquals("Doe", actual.getLastName());
        assertEquals("john@example.com", actual.getUserName());
        verify(userRepository).save(any(AppUser.class));
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void createUser_whenUserAlreadyExists_throwsUserAlreadyExistsException() {
        UserCreationDTO dto = buildCreationDTO("john@example.com", "John", "Doe", "secret123");
        AppUser existing = buildAppUser(1L, "john@example.com", "John", "Doe", "hashed");

        when(userRepository.findByUserName("john@example.com")).thenReturn(Optional.of(existing));

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(dto));
        verify(userRepository, never()).save(any());
    }

    // ── getUser ────────────────────────────────────────────────────────────────

    @Test
    void getUser_whenUserExists_returnsUserResponseDTO() {
        AppUser appUser = buildAppUser(1L, "jane@example.com", "Jane", "Smith", "hashed");
        UserResponseDTO expected = buildResponseDTO("jane@example.com", "Jane", "Smith");

        when(userRepository.findByUserName("jane@example.com")).thenReturn(Optional.of(appUser));
        when(modelMapper.map(appUser, UserResponseDTO.class)).thenReturn(expected);

        UserResponseDTO actual = userService.getUser("jane@example.com");

        assertNotNull(actual);
        assertEquals("Jane", actual.getFirstName());
        assertEquals("jane@example.com", actual.getUserName());
    }

    @Test
    void getUser_whenUserNotFound_throwsRuntimeException() {
        when(userRepository.findByUserName("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUser("unknown@example.com"));
    }

    // ── getAllUsers ─────────────────────────────────────────────────────────────

    @Test
    void getAllUsers_returnsAllUsers() {
        AppUser u1 = buildAppUser(1L, "a@example.com", "Alice", "A", "h1");
        AppUser u2 = buildAppUser(2L, "b@example.com", "Bob", "B", "h2");

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));
        when(modelMapper.map(any(AppUser.class), eq(UserResponseDTO.class))).thenReturn(new UserResponseDTO());

        List<UserResponseDTO> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_whenNoUsers_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private UserCreationDTO buildCreationDTO(String userName, String firstName, String lastName, String password) {
        UserCreationDTO dto = new UserCreationDTO();
        dto.setUserName(userName);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setPassword(password);
        return dto;
    }

    private AppUser buildAppUser(Long id, String userName, String firstName, String lastName, String password) {
        AppUser user = new AppUser();
        user.setId(id);
        user.setUserName(userName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        return user;
    }

    private UserResponseDTO buildResponseDTO(String userName, String firstName, String lastName) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserName(userName);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        return dto;
    }
}
