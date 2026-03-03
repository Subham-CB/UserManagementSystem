package za.co.userdashboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "users")
@Entity
@Getter
@Setter
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="id")
    private Long id;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "username", unique = true,nullable = false)
    private String userName;

    @Column(name = "password", nullable = false)
    private String password;

    /**
     * S3 object key for profile image (e.g. profiles/uuid-filename.jpg).
     * Full URL is built using app.s3.public-base-url + key when serving to browser.
     */
    @Column(name = "profile_image_key", length = 512)
    private String profileImageKey;
}
