package ru.practicum.shareit.user.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

public interface JpaUserRepository extends JpaRepository<User, Integer> {
}
