package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.storage.db.JpaItemRepository;
import ru.practicum.shareit.request.storage.JpaItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.storage.db.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class JpaItemRepositoryTest {
    @Autowired
    private JpaItemRepository repository;
    @Autowired
    private JpaUserRepository userRepository;
    @Autowired
    private JpaItemRequestRepository itemRequest;

    @Test
    @DirtiesContext
    void findByNameAndDescriptionShouldBeEmpty() {
        List<Item> items = repository.findByNameAndDescription("AAAA", "AAAA");

        assertTrue(items.isEmpty());
    }

    @Test
    @DirtiesContext
    void findByNameAndDescriptionShouldFindOneItem() {
        User user = new User(null, "User 1", "user1@yandex.ru");
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user, null);
        Item item2 = new Item(null, "Проводная дрель", "Проводная дрель",
                true, user, null);
        userRepository.save(user);
        repository.save(item1);
        repository.save(item2);

        assertEquals(2, repository.findAll().size());

        List<Item> items = repository.findByNameAndDescription("проводная", "проводная");

        assertEquals(1, items.size());
    }

    @Test
    @DirtiesContext
    void findByNameAndDescriptionShouldFindTwoItem() {
        User user = new User(null, "User 1", "user1@yandex.ru");
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user, null);
        Item item2 = new Item(null, "Проводная дрель", "Проводная дрель",
                true, user, null);
        userRepository.save(user);
        repository.save(item1);
        repository.save(item2);

        assertEquals(2, repository.findAll().size());

        List<Item> items = repository.findByNameAndDescription("ДРель", "ДРель");

        assertEquals(2, items.size());
    }

    @Test
    @DirtiesContext
    void findByOwnerIdOrderByIdAscShouldBeEmpty() {
        List<Item> items = repository.findByOwnerIdOrderByIdAsc(1);

        assertTrue(items.isEmpty());
    }

    @Test
    @DirtiesContext
    void findByOwnerIdOrderByIdAscTesShouldFindOneItem() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user1, null);
        Item item2 = new Item(null, "Проводная дрель", "Проводная дрель",
                true, user2, null);
        userRepository.save(user1);
        userRepository.save(user2);
        repository.save(item1);
        repository.save(item2);

        List<Item> items = repository.findByOwnerIdOrderByIdAsc(1);

        assertEquals(1, items.size());
    }

    @Test
    @DirtiesContext
    void findByOwnerIdOrderByIdAscShouldFindTwoItem() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user1, null);
        Item item2 = new Item(null, "Проводная дрель", "Проводная дрель",
                true, user1, null);
        userRepository.save(user1);
        repository.save(item1);
        repository.save(item2);

        List<Item> items = repository.findByOwnerIdOrderByIdAsc(1);

        assertEquals(2, items.size());
    }

    @Test
    @DirtiesContext
    void findByOwnerIdOrderByIdAscShouldFindOneItemAfterDelete() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user1, null);
        Item item2 = new Item(null, "Проводная дрель", "Проводная дрель",
                true, user1, null);
        userRepository.save(user1);
        repository.save(item1);
        repository.save(item2);

        List<Item> items = repository.findByOwnerIdOrderByIdAsc(1);

        assertEquals(2, items.size());

        repository.deleteById(2);
        List<Item> itemsNew = repository.findByOwnerIdOrderByIdAsc(1);

        assertEquals(1, itemsNew.size());
    }

    @Test
    @DirtiesContext
    void findByRequest_IdShouldBeEmpty() {
        List<Item> items = repository.findByRequest_Id(1);

        assertTrue(items.isEmpty());
    }

    @Test
    @DirtiesContext
    void findByRequest_IdShouldFindOne() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        userRepository.save(user1);
        ItemRequest request = new ItemRequest(null, "Request", user1, LocalDateTime.now());
        ItemRequest saveRequest = itemRequest.save(request);
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user1, saveRequest);
        Item item2 = new Item(null, "Проводная дрель", "Проводная дрель",
                true, user1, null);
        repository.save(item1);
        repository.save(item2);

        List<Item> items = repository.findByRequest_Id(1);

        assertEquals(1, items.size());
    }

    @Test
    @DirtiesContext
    void findByRequest_IdShouldFindOneOfTwo() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        ItemRequest request1 = new ItemRequest(null, "Request 1", user1, LocalDateTime.now());
        ItemRequest request2 = new ItemRequest(null, "Request 2", user2, LocalDateTime.now());
        ItemRequest saveRequest1 = itemRequest.save(request1);
        ItemRequest saveRequest2 = itemRequest.save(request2);
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user1, saveRequest1);
        Item item2 = new Item(null, "Проводная дрель", "Проводная дрель",
                true, user1, saveRequest2);
        repository.save(item1);
        repository.save(item2);

        List<Item> items = repository.findByRequest_Id(1);

        assertEquals(1, items.size());
    }

    @Test
    @DirtiesContext
    void findByRequest_IdShouldBeOk() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        ItemRequest request1 = new ItemRequest(null, "Request 1", user1, LocalDateTime.now());
        ItemRequest request2 = new ItemRequest(null, "Request 2", user2, LocalDateTime.now());
        ItemRequest request3 = new ItemRequest(null, "Request 3", user2, LocalDateTime.now());
        ItemRequest request4 = new ItemRequest(null, "Request 4", user1, LocalDateTime.now());
        ItemRequest request5 = new ItemRequest(null, "Request 5", user1, LocalDateTime.now());
        ItemRequest saveRequest1 = itemRequest.save(request1);
        ItemRequest saveRequest2 = itemRequest.save(request2);
        ItemRequest saveRequest3 = itemRequest.save(request3);
        ItemRequest saveRequest4 = itemRequest.save(request4);
        ItemRequest saveRequest5 = itemRequest.save(request5);
        Item item1 = new Item(null, "Аккумуляторная дрель", "Аккумуляторная дрель с зарядкой",
                true, user1, saveRequest1);
        Item item2 = new Item(null, "Проводная дрель", "Проводная дрель",
                true, user1, saveRequest2);
        Item item3 = new Item(null, "Молоток", "Молоток",
                true, user1, saveRequest3);
        Item item4 = new Item(null, "Питолет", "Игрушечный пистолет",
                true, user1, saveRequest4);
        Item item5 = new Item(null, "Макбук", "Макбук 2023 года",
                true, user1, saveRequest5);
        repository.save(item1);
        repository.save(item2);
        repository.save(item3);
        repository.save(item4);
        repository.save(item5);

        List<Item> items = repository.findByRequest_IdIn(List.of(1, 3, 5));

        assertEquals(5, repository.findAll().size());
        assertEquals(3, items.size());
    }
}
