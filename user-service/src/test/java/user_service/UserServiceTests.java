package user_service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTests {


    @Autowired
    private UserService userService;

    @Test
    void shouldCreateUser() {
        User user = new User();
        user.setName("XYZ");
        user.setEmail("xyz@mail.com");
        user.setRole(UserRole.HOMEOWNER);
        user.setPassword("passwordxyz");

        User savedUser = userService.createUser(user);

        assertNotNull(savedUser.getId());
        assertEquals("XYZ", savedUser.getName());
        assertEquals("xyz@mail.com", savedUser.getEmail());
        assertNotEquals("passwordxyz", savedUser.getPassword());
    }

    @Test
    void shouldFindUserById() {
        User user = new User();
        user.setName("ABC");
        user.setEmail("abc@mail.com");
        user.setRole(UserRole.HOMEOWNER);
        user.setPassword("passwordabc");

        User savedUser = userService.createUser(user);

        User foundUser = userService.getUserById(savedUser.getId());

        assertEquals("ABC", foundUser.getName());
        assertEquals("abc@mail.com", foundUser.getEmail());
    }

    @Test
    void shouldDeleteUser() {
        User user = new User();
        user.setName("123");
        user.setEmail("123@mail.com");
        user.setRole(UserRole.HOMEOWNER);
        user.setPassword("password123");

        User savedUser = userService.createUser(user);
        userService.deleteUser(savedUser.getId());

        assertThrows(org.springframework.web.server.ResponseStatusException.class, () ->
                userService.getUserById(savedUser.getId()));
    }

    @Test
    void shouldRejectInvalidEmail() {
        User user = new User();
        user.setName("XYZ");
        user.setEmail("no-email");
        user.setRole(UserRole.HOMEOWNER);
        user.setPassword("password123");

        assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> userService.createUser(user));
    }

    @Test
    void shouldRejectDuplicateEmail() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@mail.com");
        user1.setRole(UserRole.HOMEOWNER);
        user1.setPassword("password123");

        userService.createUser(user1);

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user1@mail.com");
        user2.setRole(UserRole.CONTRACTOR);
        user2.setPassword("password456");

        assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> userService.createUser(user2)
        );
    }
}