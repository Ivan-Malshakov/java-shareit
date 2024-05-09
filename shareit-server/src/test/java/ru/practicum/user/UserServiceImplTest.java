package ru.practicum.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.exceptions.DataNotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;
import ru.practicum.user.storage.db.JpaUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private UserService userService;
    @Mock
    private JpaUserRepository repository;

    @BeforeEach
    public void setUp() {
        userService = new UserService(repository, new UserMapperImpl());
    }

    @Test
    void createShouldBeOk() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        User userActual = new User(1, "User 1", "user1@yandex.ru");
        UserDto userActualDto = new UserDto(1, "User 1", "user1@yandex.ru");

        when(repository.save(any(User.class))).thenReturn(userActual);
        UserDto userCreated = userService.create(user);

        assertEquals(userActualDto, userCreated);
        verify(repository).save(any(User.class));
    }

    @Test
    void updateShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        UserDto userDto = new UserDto(null, "User 1", "user1@yandex.ru");
        Integer id = 1;

        when(repository.existsById(anyInt()))
                .thenThrow(new DataNotFoundException("User with id = " + id + " not found"));
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> userService.update(id, userDto));

        assertEquals("User with id = " + id + " not found", ex.getMessage());
    }

    @Test
    void updateShouldUpdateUserName() {
        UserDto userDto = new UserDto(null, "New user 1", null);
        Integer id = 1;
        Optional<User> lastUser = Optional.of(new User(1, "user 1", "user1@yandex.ru"));
        User userUpdate = new User(id, "New user 1", "user1@yandex.ru");
        UserDto userUpdateDto = new UserDto(id, "New user 1", "user1@yandex.ru");

        when(repository.existsById(anyInt())).thenReturn(true);
        when(repository.findById(anyInt())).thenReturn(lastUser);
        when(repository.save(any(User.class))).thenReturn(userUpdate);

        assertEquals(userUpdateDto, userService.update(id, userDto));
        verify(repository).save(any(User.class));
    }

    @Test
    void updateShouldUpdateUserEmail() {
        UserDto userDto = new UserDto(null, null, "user1update@yandex.ru");
        Integer id = 1;
        Optional<User> lastUser = Optional.of(new User(1, "User 1", "user1@yandex.ru"));
        User userUpdate = new User(id, "User 1", "user1update@yandex.ru");
        UserDto userUpdateDto = new UserDto(id, "User 1", "user1update@yandex.ru");

        when(repository.existsById(anyInt())).thenReturn(true);
        when(repository.findById(anyInt())).thenReturn(lastUser);
        when(repository.save(any(User.class))).thenReturn(userUpdate);

        assertEquals(userUpdateDto, userService.update(id, userDto));
        verify(repository).save(any(User.class));
    }

    @Test
    void updateShouldUpdateUserEmailAndName() {
        UserDto userDto = new UserDto(null, "New user 1", "user1update@yandex.ru");
        Integer id = 1;
        Optional<User> lastUser = Optional.of(new User(1, "User 1", "user1@yandex.ru"));
        User userUpdate = new User(id, "New user 1", "user1update@yandex.ru");
        UserDto userUpdateDto = new UserDto(id, "New user 1", "user1update@yandex.ru");

        when(repository.existsById(anyInt())).thenReturn(true);
        when(repository.save(any(User.class))).thenReturn(userUpdate);

        assertEquals(userUpdateDto, userService.update(id, userDto));
        verify(repository).save(any(User.class));
        verify(repository, never()).findById(anyInt());
    }

    @Test
    void updateShouldDoNothingWithAllNullParams() {
        UserDto userDto = new UserDto(null, null, null);
        Integer id = 1;
        Optional<User> lastUser = Optional.of(new User(1, "User 1", "user1@yandex.ru"));
        User userUpdate = new User(id, "User 1", "user1@yandex.ru");
        UserDto userUpdateDto = new UserDto(id, "User 1", "user1@yandex.ru");

        when(repository.findById(anyInt())).thenReturn(lastUser);
        when(repository.existsById(anyInt())).thenReturn(true);
        when(repository.save(any(User.class))).thenReturn(userUpdate);

        assertEquals(userUpdateDto, userService.update(id, userDto));
        verify(repository).save(any(User.class));
        verify(repository, atLeast(2)).findById(anyInt());
    }

    @Test
    void deleteShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer id = 1;

        when(repository.existsById(anyInt()))
                .thenThrow(new DataNotFoundException("User with id = " + id + " not found"));
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> userService.delete(anyInt()));

        assertEquals("User with id = " + id + " not found", ex.getMessage());
    }

    @Test
    void getDataShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer id = 1;

        when(repository.existsById(anyInt()))
                .thenThrow(new DataNotFoundException("User with id = " + id + " not found"));
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> userService.getData(id));

        assertEquals("User with id = " + id + " not found", ex.getMessage());
    }

    @Test
    void getDataShouldBeOk() {
        Integer id = 1;
        User user = new User(id, "New user 1", "user1update@yandex.ru");
        UserDto userDto = new UserDto(id, "New user 1", "user1update@yandex.ru");

        when(repository.existsById(anyInt())).thenReturn(true);
        when(repository.findById(anyInt())).thenReturn(Optional.of(user));

        assertEquals(userDto, userService.getData(id));
    }

    @Test
    void getAllShouldBeOkWithTwoUsers() {
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(repository.findAll()).thenReturn(users);

        assertEquals(users, userService.getAll());
    }

    @Test
    void getAllShouldReturnEmptyList() {
        List<User> users = new ArrayList<>();

        when(repository.findAll()).thenReturn(users);

        assertEquals(users, userService.getAll());
    }
}
