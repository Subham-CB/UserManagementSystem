package za.co.userdashboard.service;


import za.co.userdashboard.dto.UserCreationDTO;
import za.co.userdashboard.dto.UserResponseDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO createUser(UserCreationDTO userCreationDTO);

    UserResponseDTO getUser(String userName);

    List<UserResponseDTO> getAllUsers();
}
