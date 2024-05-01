package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService service;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Create new user");
        return service.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable @Min(1) Integer userId, @Valid @RequestBody UserDto userDto) {
        log.info("Update user with id {}.", userId);
        return service.update(userId, userDto);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Get all users.");
        return service.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable @Min(1) Integer userId) {
        log.info("Get user with id = {}.", userId);
        return service.getData(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable @Min(1) Integer userId) {
        service.delete(userId);
        log.info("User with id = {} was deleted.", userId);
    }
}
