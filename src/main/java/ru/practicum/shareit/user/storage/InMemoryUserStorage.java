package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.exceptions.ConflictEmailException;
import ru.practicum.shareit.exception.exceptions.DataNotFoundException;
import ru.practicum.shareit.exception.exceptions.ValidationException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> storage = new HashMap<>();
    private Integer generatedId = 0;

    @Override
    public User create(User user) {
        validateEmail(user);
        user.setId(++generatedId);
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Integer id, User user) {
        if (!storage.containsKey(id)) {
            throw new DataNotFoundException(String.format("User with id = %s not found.", id));
        }
        return updateUserInStorage(id, user);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public User getData(Integer id) {
        if (!storage.containsKey(id)) {
            throw new DataNotFoundException(String.format("User with id = %s not found.", id));
        }
        return storage.get(id);
    }

    @Override
    public void delete(Integer id) {
        storage.remove(id);
    }

    private void validateEmail(User user) {
        String userEmail = user.getEmail();
        if (userEmail == null) {
            throw new ValidationException("User with empty email.");
        }
        for (User userInMemory : storage.values()) {
            if (userEmail.equals(userInMemory.getEmail())) {
                throw new ConflictEmailException(String.format("User with email %s already exists.", userEmail));
            }
        }
    }

    private User updateUserInStorage(Integer id, User user) {
        User oldUser = storage.get(id);
        User updatedUser = new User();
        updatedUser.setId(id);

        if (user.getName() == null) {
            updatedUser.setName(oldUser.getName());
        } else {
            updatedUser.setName(user.getName());
        }

        if (user.getEmail() == null) {
            updatedUser.setEmail(oldUser.getEmail());
        } else if (!user.getEmail().equals(oldUser.getEmail())) {
            validateEmail(user);
            updatedUser.setEmail(user.getEmail());
        } else {
            updatedUser.setEmail(user.getEmail());
        }

        storage.put(id, updatedUser);
        return updatedUser;
    }
}
