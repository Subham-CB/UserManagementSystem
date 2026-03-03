package za.co.userdashboard.service;


import za.co.userdashboard.dto.UserCreationDTO;
import za.co.userdashboard.dto.UserResponseDTO;

import java.util.List;

public interface UserService {

    /**
     * Create a new user. Profile image key may be null if no image was uploaded.
     */
    UserResponseDTO createUser(UserCreationDTO userCreationDTO, String profileImageKey);

    UserResponseDTO getUser(String userName);

    List<UserResponseDTO> getAllUsers();
}
