package ru.practicum.user.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.User;

public interface JpaUserRepository extends JpaRepository<User, Integer> {
}
