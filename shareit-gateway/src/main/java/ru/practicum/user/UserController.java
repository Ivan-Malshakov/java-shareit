package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping()
    public ResponseEntity<Object> getAllUser() {
        log.info("Get all users.");
        return userClient.getAllUser();
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable @Min(1) Integer userId) {
        log.info("Get user with id = {}.", userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserDto request) {
        log.info("Create new user {}", request);
        return userClient.saveUser(request);
    }

    @PatchMapping(value = "/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable @Min(1) Integer userId, @RequestBody UserDto request) {
        log.info("Update user with id {}.", userId);
        return userClient.updateUser(userId, request);
    }

    @DeleteMapping(value = "/{userId}")
    public void removeUser(@PathVariable @Min(1) Integer userId) {
        log.info("User with id = {} was deleted.", userId);
        userClient.removeUser(userId);
    }
}
