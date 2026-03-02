package za.co.userdashboard.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import za.co.userdashboard.config.MapperConfig;
import za.co.userdashboard.dto.UserCreationDTO;
import za.co.userdashboard.dto.UserResponseDTO;
import za.co.userdashboard.entity.AppUser;
import za.co.userdashboard.exception.UserAlreadyExistsException;
import za.co.userdashboard.repository.UserRepository;
import za.co.userdashboard.service.UserService;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final UserService userService;

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
                               RedirectAttributes redirectAttributes) {


        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (!user.getPassword().equals(confirmPassword)) {
            bindingResult.rejectValue("password", "error.user", "Passwords do not match");
            return "register";
        }
        try {
             userService.createUser(user);
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


}
