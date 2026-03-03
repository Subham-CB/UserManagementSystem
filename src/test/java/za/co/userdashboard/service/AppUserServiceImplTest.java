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
import za.co.userdashboard.repository.UserRepository;
import za.co.userdashboard.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppUserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_success_returnsUserResponseDTO(){

        UserCreationDTO newUser =  buildCreationDTO("john@example.com", "John", "Doe", "admin123");
        AppUser savedAppUser =  buildAppUser(1L, "john@example.com", "John", "Doe", "admin123");
        UserResponseDTO expectedResponse = buildResponseDTO("john@example.com", "John", "Doe");

        when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$87MzvyxiALng/TE1rxnlUeIxuKdnHy0XnX77ZI36XBadnPJun8qqq");
        when(modelMapper.map(any(UserCreationDTO.class),eq(AppUser.class))).thenReturn(savedAppUser);
        when(userRepository.save(any(AppUser.class))).thenReturn(savedAppUser);
        when(modelMapper.map(any(AppUser.class),eq(UserResponseDTO.class))).thenReturn(expectedResponse);

        UserResponseDTO actualResponse = userService.createUser(newUser, null);

        assertNotNull(actualResponse);
        assertEquals("John",actualResponse.getFirstName());
        assertEquals("Doe",actualResponse.getLastName());
        assertEquals("john@example.com",actualResponse.getUserName());

        verify(userRepository,times(1)).save(any(AppUser.class));
        verify(passwordEncoder).encode(anyString());

    }

    @Test
    void getUser_whenUserExists_returnsUserResponseDTO() {

        AppUser appUser = buildAppUser(1L, "jane@example.com", "Jane", "Smith", "admin123");
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

        when(userRepository.findByUserName("abc@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUser("abc@example.com"));
    }

    @Test
    void getAllUsers_returnsAllUsers(){

        AppUser appUser1 = buildAppUser(1L, "jane@example.com", "Jane", "Smith", "admin123");

        AppUser appUser2 = buildAppUser(2L, "john@example.com", "John", "Doe", "admin123");

        when(userRepository.findAll()).thenReturn(List.of(appUser1, appUser2));
        when(modelMapper.map(any(AppUser.class),eq(UserResponseDTO.class))).thenReturn(new UserResponseDTO());

        List<UserResponseDTO> users = userService.getAllUsers();

        assertEquals(2,users.size());
        verify(userRepository,times(1)).findAll();
    }

    @Test
    void getAllUsers_whenNoUsers_returnsEmptyList() {

        when(userRepository.findAll()).thenReturn(List.of());
        List<UserResponseDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    //Helpers

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
