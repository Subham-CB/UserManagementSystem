package com.example.service;

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
    void createUser(){

        UserCreationDTO newUser =  new UserCreationDTO();
        newUser.setFirstName("Subham");
        newUser.setLastName("Dey");
        newUser.setUserName("de.se@gmail.com");
        newUser.setPassword("admin123");

        AppUser savedAppUser =  new AppUser();
        savedAppUser.setId(1L);
        savedAppUser.setFirstName("Subham");
        savedAppUser.setLastName("Dey");
        savedAppUser.setUserName("de.se@gmail.com");
        savedAppUser.setPassword("admin123");

        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setFirstName("Subham");
        expectedResponse.setLastName("Dey");
        expectedResponse.setUserName("de.se@gmail.com");

        when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$87MzvyxiALng/TE1rxnlUeIxuKdnHy0XnX77ZI36XBadnPJun8qqq");
        when(modelMapper.map(any(UserCreationDTO.class),eq(AppUser.class))).thenReturn(savedAppUser);
        when(userRepository.save(any(AppUser.class))).thenReturn(savedAppUser);
        when(modelMapper.map(any(AppUser.class),eq(UserResponseDTO.class))).thenReturn(expectedResponse);

        UserResponseDTO actualResponse = userService.createUser(newUser);

        assertNotNull(actualResponse);
        assertEquals("Subham",actualResponse.getFirstName());
        assertEquals("Dey",actualResponse.getLastName());
        assertEquals("de.se@gmail.com",actualResponse.getUserName());

        verify(userRepository,times(1)).save(any(AppUser.class));

    }

    @Test
    void getAllUsers(){

        AppUser appUser1 = new AppUser();
        appUser1.setId(1L);
        appUser1.setFirstName("Subham1");
        appUser1.setLastName("Dey");
        appUser1.setUserName("a.b@gmail.com");

        AppUser appUser2 = new AppUser();
        appUser2.setId(2L);
        appUser2.setFirstName("Subham2");
        appUser1.setLastName("Dey");
        appUser2.setUserName("b.b@gmail.com");

        when(userRepository.findAll()).thenReturn(List.of(appUser1, appUser2));
        when(modelMapper.map(any(AppUser.class),eq(UserResponseDTO.class))).thenReturn(new UserResponseDTO());

        List<UserResponseDTO> users = userService.getAllUsers();

        assertEquals(2,users.size());
        verify(userRepository,times(1)).findAll();
    }




}
