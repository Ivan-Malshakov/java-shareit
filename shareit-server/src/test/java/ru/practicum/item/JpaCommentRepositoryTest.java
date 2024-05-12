package ru.practicum.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.item.storage.db.JpaCommentRepository;
import ru.practicum.item.storage.db.JpaItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.storage.db.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class JpaCommentRepositoryTest {
    @Autowired
    private JpaCommentRepository repository;
    @Autowired
    private JpaUserRepository userRepository;
    @Autowired
    private JpaItemRepository itemRepository;

    @Test
    @DirtiesContext
    void findCommentByItem_IdTestOneItemShouldBeOk() {
        User user = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        Item item1 = new Item(null, "Some item", "Description of item",
                true, user, null);
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item1);
        Comment comment = new Comment(null, "Text", user2, item1, LocalDateTime.now());
        repository.save(comment);

        List<Comment> comments = repository.findCommentByItem_Id(1);

        assertEquals(1, comments.size());
    }

    @Test
    @DirtiesContext
    void findCommentByItem_IdTestEmpty() {
        User user = new User(null, "User1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        Item item1 = new Item(null, "Some item", "Description of item",
                true, user, null);
        userRepository.save(user);
        userRepository.save(user2);
        itemRepository.save(item1);
        Comment comment = new Comment(null, "Text", user2, item1, LocalDateTime.now());
        repository.save(comment);

        List<Comment> comments = repository.findCommentByItem_Id(2);

        assertEquals(0, comments.size());
    }

    @Test
    @DirtiesContext
    void findCommentByItem_IdTestTwoItemShouldBeOk() {
        User user = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        User user3 = new User(null, "User 3", "user3@yandex.ru");
        Item item1 = new Item(null, "Some item", "Description of item",
                true, user, null);
        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(user3);
        itemRepository.save(item1);
        Comment comment1 = new Comment(null, "Text", user2, item1, LocalDateTime.now());
        Comment comment2 = new Comment(null, "Text", user3, item1, LocalDateTime.now());
        repository.save(comment1);
        repository.save(comment2);

        List<Comment> comments = repository.findCommentByItem_Id(1);

        assertEquals(2, comments.size());
    }
}
