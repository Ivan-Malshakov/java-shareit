package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public User create(User data) {
        return storage.create(data);
    }

    public User update(Integer id, User user) {
        return storage.update(id, user);
    }

    public List<User> getAll() {
        return storage.getAll();
    }

    public User getData(Integer id) {
        return storage.getData(id);
    }

    public void delete(Integer id) {
        storage.delete(id);
    }
}
