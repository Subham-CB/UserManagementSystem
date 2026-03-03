package za.co.userdashboard.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import za.co.userdashboard.config.SecurityConfig;
import za.co.userdashboard.dto.UserResponseDTO;
import za.co.userdashboard.exception.UserAlreadyExistsException;
import za.co.userdashboard.security.CustomerUserDetailsService;
import za.co.userdashboard.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
@Import(SecurityConfig.class)
class WebControllerTest {

    @Autowired
    private  MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomerUserDetailsService customerUserDetailsService;


    @Test
    void getLoginPage_returnsLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }


    @Test
    void getRegisterPage_returnsRegisterViewWithUserAttribute() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }


    @Test
    void postRegister_validData_redirectsToLogin() throws Exception {
        UserResponseDTO response = new UserResponseDTO();
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setUserName("john@example.com");

        when(userService.createUser(any())).thenReturn(response);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("userName", "john@example.com")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void postRegister_passwordsDoNotMatch_returnsRegisterView() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("userName", "john@example.com")
                        .param("password", "secret123")
                        .param("confirmPassword", "different"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void postRegister_blankFirstName_returnsRegisterViewWithErrors() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "")
                        .param("lastName", "Doe")
                        .param("userName", "john@example.com")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void postRegister_passwordTooShort_returnsRegisterViewWithErrors() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("userName", "john@example.com")
                        .param("password", "short")
                        .param("confirmPassword", "short"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void postRegister_userAlreadyExists_returnsRegisterViewWithError() throws Exception {
        when(userService.createUser(any()))
                .thenThrow(new UserAlreadyExistsException("This email is already taken"));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("userName", "john@example.com")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }


    @Test
    void getDashboard_withAuthenticatedUser_returnsDashboardView() throws Exception {
        UserResponseDTO response = new UserResponseDTO();
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setUserName("john@example.com");

        when(userService.getUser("john@example.com")).thenReturn(response);

        mockMvc.perform(get("/dashboard")
                        .with(user("john@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("user"));
    }
}