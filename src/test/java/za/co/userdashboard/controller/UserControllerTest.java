package za.co.userdashboard.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import za.co.userdashboard.config.SecurityConfig;
import za.co.userdashboard.dto.UserResponseDTO;
import za.co.userdashboard.security.CustomerUserDetailsService;
import za.co.userdashboard.service.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomerUserDetailsService customerUserDetailsService;

    @Test
    void getAllUsers_withAuthenticatedUser_returnsOkAndUserList() throws Exception {
        UserResponseDTO u1 = new UserResponseDTO();
        u1.setFirstName("Alice");
        u1.setLastName("A");
        u1.setUserName("alice@example.com");

        UserResponseDTO u2 = new UserResponseDTO();
        u2.setFirstName("Bob");
        u2.setLastName("B");
        u2.setUserName("bob@example.com");

        when(userService.getAllUsers()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/user/fetch")
                        .with(user("alice@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].firstName").value("Bob"));
    }

    @Test
    void getAllUsers_withEmptyList_returnsOkAndEmptyArray() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/user/fetch")
                        .with(user("alice@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllUsers_withoutAuthentication_returnsRedirectToLogin() throws Exception {
        mockMvc.perform(get("/user/fetch"))
                .andExpect(status().is3xxRedirection());
    }
}
