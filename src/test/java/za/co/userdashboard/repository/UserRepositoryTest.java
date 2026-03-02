package za.co.userdashboard.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import za.co.userdashboard.entity.AppUser;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUserName_whenUserExists_returnsUser() {
        AppUser user = new AppUser();
        user.setFirstName("Alice");
        user.setLastName("A");
        user.setUserName("alice@example.com");
        user.setPassword("hashed");
        entityManager.persistAndFlush(user);

        Optional<AppUser> result = userRepository.findByUserName("alice@example.com");

        assertTrue(result.isPresent());
        assertEquals("alice@example.com", result.get().getUserName());
        assertEquals("Alice", result.get().getFirstName());
    }

    @Test
    void findByUserName_whenUserDoesNotExist_returnsEmpty() {
        Optional<AppUser> result = userRepository.findByUserName("nobody@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    void save_persistsUserAndAssignsId() {
        AppUser user = new AppUser();
        user.setFirstName("Bob");
        user.setLastName("B");
        user.setUserName("bob@example.com");
        user.setPassword("hashed");

        AppUser saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("bob@example.com", saved.getUserName());
    }

    @Test
    void findAll_returnsAllPersistedUsers() {
        AppUser u1 = new AppUser();
        u1.setFirstName("Carol");
        u1.setLastName("C");
        u1.setUserName("carol@example.com");
        u1.setPassword("h1");

        AppUser u2 = new AppUser();
        u2.setFirstName("Dave");
        u2.setLastName("D");
        u2.setUserName("dave@example.com");
        u2.setPassword("h2");

        entityManager.persistAndFlush(u1);
        entityManager.persistAndFlush(u2);

        List<AppUser> users = userRepository.findAll();

        assertTrue(users.size() >= 2);
    }

    @Test
    void findByUserName_isCaseSensitive() {
        AppUser user = new AppUser();
        user.setFirstName("Eve");
        user.setLastName("E");
        user.setUserName("eve@example.com");
        user.setPassword("hashed");
        entityManager.persistAndFlush(user);

        Optional<AppUser> upperResult = userRepository.findByUserName("EVE@EXAMPLE.COM");
        Optional<AppUser> lowerResult = userRepository.findByUserName("eve@example.com");

        assertFalse(upperResult.isPresent());
        assertTrue(lowerResult.isPresent());
    }
}
