package za.co.userdashboard.service.impl;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.s3.public-base-url:}")
    private String s3PublicBaseUrl;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreationDTO userCreationDTO, String profileImageKey) {

        if (userRepository.findByUserName(userCreationDTO.getUserName()).isPresent()) {
            throw new UserAlreadyExistsException("This email is already taken");
        }
        AppUser appUser = modelMapper.map(userCreationDTO, AppUser.class);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        if (StringUtils.hasText(profileImageKey)) {
            appUser.setProfileImageKey(profileImageKey);
        }
        AppUser savedAppUser = userRepository.save(appUser);
        log.info("User : {} has been created", appUser.getFirstName());
        return toResponseDTO(savedAppUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUser(String userName) {

        AppUser user =  userRepository.findByUserName(userName)
                .orElseThrow(()->new RuntimeException("User not found"));
        log.info("Fetched User : {}",user.getFirstName());
        return toResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {

        List<AppUser> users =  userRepository.findAll();
        log.info("Fetched all the users");
        return users.stream().map(this::toResponseDTO).toList();
    }

    private UserResponseDTO toResponseDTO(AppUser user) {
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);
        if (!StringUtils.hasText(user.getProfileImageKey())) {
            return dto;
        }
        dto.setProfileImageKey(user.getProfileImageKey());
        if (StringUtils.hasText(s3PublicBaseUrl)) {
            String base = s3PublicBaseUrl.endsWith("/") ? s3PublicBaseUrl : s3PublicBaseUrl + "/";
            dto.setProfileImageUrl(base + user.getProfileImageKey());
        } else {
            dto.setProfileImageUrl("/profile-image?key=" + URLEncoder.encode(user.getProfileImageKey(), StandardCharsets.UTF_8));
        }
        return dto;
    }
}
