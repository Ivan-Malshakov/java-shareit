package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.exceptions.DataNotFoundException;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.storage.db.JpaUserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final JpaUserRepository storage;
    private final UserMapper userMapper;

    @Transactional
    public UserDto create(UserDto data) {
        User user = userMapper.toUser(data);
        return userMapper.toUserDto(storage.save(user));
    }

    @Transactional
    public UserDto update(Integer id, UserDto request) {
        if (!storage.existsById(id)) {
            log.warn("User with id = {} not found", id);
            throw new DataNotFoundException(String.format("User with id = %s not found", id));
        }

        User user = userMapper.toUser(request);
        user.setId(id);
        if (user.getEmail() == null) {
            user.setEmail(storage.findById(id).get().getEmail());
        }
        if (user.getName() == null) {
            user.setName(storage.findById(id).get().getName());
        }
        User updatedUser = storage.save(user);
        return userMapper.toUserDto(updatedUser);
    }

    public List<User> getAll() {
        return storage.findAll();
    }

    public UserDto getData(Integer id) {
        if (!storage.existsById(id)) {
            log.warn("User with id = {} not found.", id);
            throw new DataNotFoundException(String.format("User with id = %s not found", id));
        }
        return userMapper.toUserDto(storage.findById(id).get());
    }

    @Transactional
    public void delete(Integer id) {
        if (!storage.existsById(id)) {
            log.warn("User with id = {} not found", id);
            throw new DataNotFoundException(String.format("User with id = %s not found", id));
        }
        storage.deleteById(id);
    }
}
