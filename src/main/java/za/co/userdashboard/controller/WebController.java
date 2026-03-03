package za.co.userdashboard.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import za.co.userdashboard.service.ImageStreamResult;
import za.co.userdashboard.dto.UserCreationDTO;
import za.co.userdashboard.dto.UserResponseDTO;
import za.co.userdashboard.entity.AppUser;
import za.co.userdashboard.exception.UserAlreadyExistsException;
import za.co.userdashboard.service.ProfileImageService;
import za.co.userdashboard.service.UserService;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final UserService userService;
    private final ProfileImageService profileImageService;

    /**
     *  Login form
     */
  @GetMapping("/login")
  public String login(){
    return "login";
  }

    /**
     *  Registration form
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserCreationDTO());
        return "register";
    }

    /**
     * User registration
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserCreationDTO user,
                               BindingResult bindingResult,
                               @RequestParam(name = "confirmPassword") String confirmPassword,
                               @RequestParam(name = "profileImage", required = false) MultipartFile profileImage,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (!user.getPassword().equals(confirmPassword)) {
            bindingResult.rejectValue("password", "error.user", "Passwords do not match");
            return "register";
        }
        String profileImageKey = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                profileImageKey = profileImageService.uploadProfileImage(profileImage);
            } catch (IllegalArgumentException e) {
                bindingResult.rejectValue("userName", "error.user", "Invalid profile image. Use JPEG, PNG, GIF or WebP.");
                return "register";
            } catch (Exception e) {
                bindingResult.rejectValue("userName", "error.user", "Failed to upload profile image. Please try again.");
                return "register";
            }
        }
        try {
            userService.createUser(user, profileImageKey);
            redirectAttributes.addFlashAttribute("message", "Registration successful!");
            return "redirect:/login";
        } catch (UserAlreadyExistsException e) {
            bindingResult.rejectValue("userName", "error.user", e.getMessage());
            return "register";
        }
    }


    /**
     *  Dashboard
     */
  @GetMapping("/dashboard")
  public String registerUser(Model model,Authentication authentication){

      UserResponseDTO response = userService.getUser(authentication.getName());
      model.addAttribute("user",response);
      return "dashboard";

  }

    /**
     * Serve profile image from S3 (proxy). Key must start with profiles/.
     */
    @GetMapping("/profile-image")
    public ResponseEntity<InputStreamResource> profileImage(@RequestParam("key") String key) {
        ImageStreamResult result = profileImageService.getProfileImage(key);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        var body = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.contentType()))
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=3600");
        if (result.contentLength() != null && result.contentLength() > 0) {
            body.contentLength(result.contentLength());
        }
        return body.body(new InputStreamResource(result.inputStream()));
    }
}
