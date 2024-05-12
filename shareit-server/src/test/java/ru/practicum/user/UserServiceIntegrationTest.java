package ru.practicum.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.exception.exceptions.DataNotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    void createShouldBeOk() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        User userExpected = new User(1, "User 1", "user1@yandex.ru");

        UserDto userCreated = userService.create(user);

        assertEquals(userExpected.getId(), userCreated.getId());
        assertEquals(userExpected.getName(), userCreated.getName());
        assertEquals(userExpected.getEmail(), userCreated.getEmail());
    }

    @Test
    @DirtiesContext
    void updateShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto userUpdateDto = new UserDto(null, "New user 1", "user1@yandex.ru");
        userService.create(user);

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> userService.update(2, userUpdateDto));

        assertEquals("User with id = " + 2 + " not found", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void updateShouldBeOkWithNewName() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto userUpdateDto = new UserDto(null, "New user 1", null);
        userService.create(user);

        UserDto userUpdate = userService.update(1, userUpdateDto);
        User userExpected = new User(1, "New user 1", "user1@yandex.ru");

        assertEquals(userExpected.getId(), userUpdate.getId());
        assertEquals(userExpected.getName(), userUpdate.getName());
        assertEquals(userExpected.getEmail(), userUpdate.getEmail());
    }

    @Test
    @DirtiesContext
    void updateShouldBeOkWithNewEmail() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto userUpdateDto = new UserDto(null, null, "userupdate1@yandex.ru");
        userService.create(user);

        UserDto userUpdate = userService.update(1, userUpdateDto);
        User userExpected = new User(1, "User 1", "userupdate1@yandex.ru");

        assertEquals(userExpected.getId(), userUpdate.getId());
        assertEquals(userExpected.getName(), userUpdate.getName());
        assertEquals(userExpected.getEmail(), userUpdate.getEmail());
    }

    @Test
    @DirtiesContext
    void updateShouldBeOkWithNewNameAndEmail() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto userUpdateDto = new UserDto(null, "New user 1", "userupdate1@yandex.ru");
        userService.create(user);

        UserDto userUpdate = userService.update(1, userUpdateDto);
        User userExpected = new User(1, "New user 1", "userupdate1@yandex.ru");

        assertEquals(userExpected.getId(), userUpdate.getId());
        assertEquals(userExpected.getName(), userUpdate.getName());
        assertEquals(userExpected.getEmail(), userUpdate.getEmail());
    }

    @Test
    @DirtiesContext
    void updateShouldDoNothingWithAllNullParams() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto userUpdateDto = new UserDto(null, null, null);
        userService.create(user);

        UserDto userUpdate = userService.update(1, userUpdateDto);
        User userExpected = new User(1, "User 1", "user1@yandex.ru");

        assertEquals(userExpected.getId(), userUpdate.getId());
        assertEquals(userExpected.getName(), userUpdate.getName());
        assertEquals(userExpected.getEmail(), userUpdate.getEmail());
    }

    @Test
    @DirtiesContext
    void deleteShouldBeOk() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        userService.create(user);

        assertEquals(1, userService.getAll().size());

        userService.delete(1);

        assertEquals(0, userService.getAll().size());
    }

    @Test
    @DirtiesContext
    void deleteShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        userService.create(user);

        assertEquals(1, userService.getAll().size());
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> userService.delete(2)
        );

        assertEquals("User with id = " + 2 + " not found", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getDataShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        userService.create(user);

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> userService.getData(2)
        );

        assertEquals("User with id = " + 2 + " not found", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getDataShouldBeOk() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        userService.create(user);
        UserDto userCreated = userService.getData(1);
        User userExpected = new User(1, "User 1", "user1@yandex.ru");

        assertEquals(userExpected.getId(), userCreated.getId());
        assertEquals(userExpected.getName(), userCreated.getName());
        assertEquals(userExpected.getEmail(), userCreated.getEmail());
    }

    @Test
    @DirtiesContext
    void getDataShouldThrowDataNotFoundExceptionAfterRemoveUser() {
        UserDto userDto = new UserDto(null, "User 1", "user1@yandex.ru");
        userService.create(userDto);
        UserDto user = userService.getData(1);

        assertEquals(1, userService.getAll().size());

        userService.delete(1);
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> userService.getData(1)
        );

        assertEquals("User with id = " + 1 + " not found", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getAllShouldReturnEmptyList() {
        assertEquals(0, userService.getAll().size());
    }

    @Test
    @DirtiesContext
    void getAllShouldBeOkWithTwoUsers() {
        UserDto user1 = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto user2 = new UserDto(null, "User 2", "user2@yandex.ru");
        userService.create(user1);
        userService.create(user2);

        assertEquals(2, userService.getAll().size());
    }

    @Test
    @DirtiesContext
    void getAllShouldReturnOneUserAfterRemoveUser() {
        UserDto user1 = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto user2 = new UserDto(null, "User 2", "user2@yandex.ru");
        userService.create(user1);
        userService.create(user2);

        assertEquals(2, userService.getAll().size());

        userService.delete(1);

        assertEquals(1, userService.getAll().size());
    }
}
