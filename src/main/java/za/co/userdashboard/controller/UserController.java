package za.co.userdashboard.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import za.co.userdashboard.dto.UserCreationDTO;
import za.co.userdashboard.dto.UserResponseDTO;
import za.co.userdashboard.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;


//todo: impl swagger

    @GetMapping("/fetch")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(){

        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);

    }

}
