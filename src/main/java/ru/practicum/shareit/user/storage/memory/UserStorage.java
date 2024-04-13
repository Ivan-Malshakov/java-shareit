package ru.practicum.shareit.user.storage.memory;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    User create(User data);

    User update(Integer id, User user);

    List<User> getAll();

    User getData(Integer id);

    void delete(Integer id);
}
