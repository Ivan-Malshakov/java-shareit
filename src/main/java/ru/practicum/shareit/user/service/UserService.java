package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.exceptions.DataNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.db.JpaUserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final JpaUserRepository storage;
    private final UserMapper userMapper;

    @Transactional
    public User create(User data) {
        return storage.save(data);
    }

    @Transactional
    public User update(Integer id, UserDto request) {
        if (!storage.existsById(id)) {
            log.warn("User with id = {} not found.", id);
            throw new DataNotFoundException(String.format("User with id = %s not found.", id));
        }

        User user = userMapper.toUser(request);
        user.setId(id);
        if (user.getEmail() == null) {
            user.setEmail(storage.findById(id).get().getEmail());
        }
        if (user.getName() == null) {
            user.setName(storage.findById(id).get().getName());
        }
        return storage.save(user);
    }

    public List<User> getAll() {
        return storage.findAll();
    }

    public User getData(Integer id) {
        if (!storage.existsById(id)) {
            log.warn("User with id = {} not found.", id);
            throw new DataNotFoundException(String.format("User with id = %s not found.", id));
        }
        return storage.findById(id).get();
    }

    @Transactional
    public void delete(Integer id) {
        storage.deleteById(id);
    }
}
