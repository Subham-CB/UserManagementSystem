package za.co.userdashboard.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import za.co.userdashboard.entity.AppUser;
import za.co.userdashboard.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerAndFetchUser_fullFlow() throws Exception {
        // Register a new user via POST /register
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "Integration")
                        .param("lastName", "Test")
                        .param("userName", "integration@example.com")
                        .param("password", "password1")
                        .param("confirmPassword", "password1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // Fetch the registered user via GET /user/fetch (authenticated)
        mockMvc.perform(get("/user/fetch")
                        .with(user("integration@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.userName=='integration@example.com')]").exists());
    }

    @Test
    void register_duplicateUserName_staysOnRegisterPage() throws Exception {
        // Pre-create a user
        AppUser existing = new AppUser();
        existing.setFirstName("Existing");
        existing.setLastName("User");
        existing.setUserName("duplicate@example.com");
        existing.setPassword(passwordEncoder.encode("password1"));
        userRepository.save(existing);

        // Attempt to register with the same username
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "Another")
                        .param("lastName", "Person")
                        .param("userName", "duplicate@example.com")
                        .param("password", "password1")
                        .param("confirmPassword", "password1"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void fetchAllUsers_withoutAuthentication_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/user/fetch"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void dashboard_withAuthenticatedExistingUser_returnsDashboard() throws Exception {
        AppUser user = new AppUser();
        user.setFirstName("Dashboard");
        user.setLastName("User");
        user.setUserName("dashboard@example.com");
        user.setPassword(passwordEncoder.encode("password1"));
        userRepository.save(user);

        mockMvc.perform(get("/dashboard")
                        .with(user("dashboard@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("user"));
    }
}
