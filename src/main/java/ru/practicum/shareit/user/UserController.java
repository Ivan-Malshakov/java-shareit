package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService service;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User createdUser = service.create(user);
        log.info("Create user {}.", createdUser);
        return userMapper.toUserDto(createdUser);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Integer userId, @Valid @RequestBody UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User updatedUser = service.update(userId, user);
        log.info("Update user {}.", updatedUser);
        return userMapper.toUserDto(updatedUser);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Get all users.");
        return service.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Integer userId) {
        User user = service.getData(userId);
        log.info("Get user with id = {}.", userId);
        return userMapper.toUserDto(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Integer userId) {
        service.delete(userId);
        log.info("User with id = {} was deleted.", userId);
    }
}
