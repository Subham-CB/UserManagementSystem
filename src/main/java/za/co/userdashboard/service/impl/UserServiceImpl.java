package za.co.userdashboard.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import za.co.userdashboard.exception.UserAlreadyExistsException;
import za.co.userdashboard.dto.UserCreationDTO;
import za.co.userdashboard.dto.UserResponseDTO;
import za.co.userdashboard.entity.AppUser;
import za.co.userdashboard.repository.UserRepository;
import za.co.userdashboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {



    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreationDTO userCreationDTO) {

        if (userRepository.findByUserName(userCreationDTO.getUserName()).isPresent()) {
            throw new UserAlreadyExistsException("This email is already taken");
        }
        AppUser appUser = modelMapper.map(userCreationDTO, AppUser.class);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        AppUser savedAppUser = userRepository.save(appUser);
        log.info("User : {} has been created", appUser.getFirstName());
        return modelMapper.map(savedAppUser,UserResponseDTO.class);

    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUser(String userName) {

        AppUser user =  userRepository.findByUserName(userName)
                .orElseThrow(()->new RuntimeException("User not found"));
        log.info("Fetched User : {}",user.getFirstName());
        return modelMapper.map(user,UserResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {

        List<AppUser> users =  userRepository.findAll();
        log.info("Fetched all the users");
        return users.stream().map(user -> modelMapper.map(user,UserResponseDTO.class)).toList();

    }
}
