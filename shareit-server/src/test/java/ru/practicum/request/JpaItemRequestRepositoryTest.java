package ru.practicum.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.request.storage.JpaItemRequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.storage.db.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class JpaItemRequestRepositoryTest {
    @Autowired
    private JpaItemRequestRepository repository;

    @Autowired
    JpaUserRepository userRepository;

    @Test
    @DirtiesContext
    void findItemRequestByRequestor_IdOrderByCreatedDescShouldBeOk() {
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime created2 = LocalDateTime.now().plusDays(30);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime created4 = LocalDateTime.now().plusDays(20);
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        ItemRequest request1 = new ItemRequest(null, "Request 1", user1, created1);
        ItemRequest request2 = new ItemRequest(null, "Request 2", user1, created2);
        ItemRequest request3 = new ItemRequest(null, "Request 3", user2, created3);
        ItemRequest request4 = new ItemRequest(null, "Request 4", user1, created4);
        ItemRequest requestResponse1 = new ItemRequest(1, "Request 1", user1, created1);
        ItemRequest requestResponse2 = new ItemRequest(2, "Request 2", user1, created2);
        ItemRequest requestResponse3 = new ItemRequest(3, "Request 3", user2, created3);
        ItemRequest requestResponse4 = new ItemRequest(4, "Request 4", user1, created4);
        repository.save(request1);
        repository.save(request2);
        repository.save(request3);
        repository.save(request4);

        assertEquals(4, repository.findAll().size());

        List<ItemRequest> requestList = repository.findItemRequestByRequestor_IdOrderByCreatedDesc(1);

        assertEquals(3, requestList.size());
        assertEquals(requestResponse2.getId(), requestList.get(0).getId());
        assertEquals(requestResponse2.getDescription(), requestList.get(0).getDescription());
        assertEquals(requestResponse2.getCreated(), requestList.get(0).getCreated());
        assertEquals(requestResponse4.getId(), requestList.get(1).getId());
        assertEquals(requestResponse4.getDescription(), requestList.get(1).getDescription());
        assertEquals(requestResponse4.getCreated(), requestList.get(1).getCreated());
        assertEquals(requestResponse1.getId(), requestList.get(2).getId());
        assertEquals(requestResponse1.getDescription(), requestList.get(2).getDescription());
        assertEquals(requestResponse1.getCreated(), requestList.get(2).getCreated());
    }

    @Test
    @DirtiesContext
    void findItemRequestNotByRequestor_IdOrderByCreatedShouldBeOk() {
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime created2 = LocalDateTime.now().plusDays(30);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime created4 = LocalDateTime.now().plusDays(20);
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        User user3 = new User(null, "User 3", "user3@yandex.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        ItemRequest request1 = new ItemRequest(null, "Request 1", user1, created1);
        ItemRequest request2 = new ItemRequest(null, "Request 2", user2, created2);
        ItemRequest request3 = new ItemRequest(null, "Request 3", user3, created3);
        ItemRequest request4 = new ItemRequest(null, "Request 4", user1, created4);
        ItemRequest requestResponse2 = new ItemRequest(2, "Request 2", user2, created2);
        ItemRequest requestResponse3 = new ItemRequest(3, "Request 3", user3, created3);
        repository.save(request1);
        repository.save(request2);
        repository.save(request3);
        repository.save(request4);

        assertEquals(4, repository.findAll().size());

        List<ItemRequest> requestList = repository.findItemRequestNotByRequestor_IdOrderByCreatedDesc(1);

        assertEquals(2, requestList.size());
        assertEquals(requestResponse3.getId(), requestList.get(0).getId());
        assertEquals(requestResponse3.getDescription(), requestList.get(0).getDescription());
        assertEquals(requestResponse3.getCreated(), requestList.get(0).getCreated());
        assertEquals(requestResponse2.getId(), requestList.get(1).getId());
        assertEquals(requestResponse2.getDescription(), requestList.get(1).getDescription());
        assertEquals(requestResponse2.getCreated(), requestList.get(1).getCreated());
    }
}
