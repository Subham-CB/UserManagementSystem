package za.co.userdashboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreationDTO {
    //todo: dto level validation


    @NotBlank(message = "First Name cannot be blank")
    private String firstName;
    @NotBlank(message = "Last Name cannot be blank")
    private String lastName;
    @NotBlank(message = "Username cannot be blank")
    private String userName;
    @Size(min=8,max = 20,message = "Enter Password with length 8-20")
    @NotBlank(message = "Password can not be blank")
    private String password;
}
