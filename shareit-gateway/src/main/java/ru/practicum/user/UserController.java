package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping()
    public ResponseEntity<Object> getAllUser() {
        log.debug("Get all users.");
        return userClient.getAllUser();
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable @Min(1) Integer userId) {
        log.debug("Get user with id = {}.", userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> saveUser(@NotNull @Valid @RequestBody UserDto request) {
        log.debug("Create new user {}", request);
        return userClient.saveUser(request);
    }

    @PatchMapping(value = "/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable @Min(1) Integer userId,
                                             @NotNull @RequestBody UserDto request) {
        log.debug("Update user with id {}.", userId);
        return userClient.updateUser(userId, request);
    }

    @DeleteMapping(value = "/{userId}")
    public void removeUser(@PathVariable @Min(1) Integer userId) {
        log.debug("User with id = {} was deleted.", userId);
        userClient.removeUser(userId);
    }
}
