package za.co.userdashboard.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    private String firstName;
    private String lastName;
    private String userName;
    /** Full URL for profile image (null if not set). Used when linking to external S3. */
    private String profileImageUrl;
    /** S3 object key for profile image (e.g. profiles/uuid.jpg). Used to build proxy URL in template. */
    private String profileImageKey;
}
