package ru.practicum.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.exception.exceptions.DataNotFoundException;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemToRequestResponse;
import ru.practicum.item.service.ItemService;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestsResponseDto;
import ru.practicum.request.service.ItemRequestService;
import ru.practicum.request.storage.JpaItemRequestRepository;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemRequestServiceIntegrationTest {
    @Autowired
    private ItemRequestService requestService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private JpaItemRequestRepository requestJpa;

    @Test
    @DirtiesContext
    void saveItemRequestShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer userId = 1;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Desc 1");

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> requestService.saveItemRequest(userId, requestDto));

        assertEquals("User with id = " + userId + " not found", ex.getMessage());
        assertEquals(0, requestJpa.findAll().size());
    }

    @Test
    @DirtiesContext
    void saveItemRequestShouldBeOk() {
        Integer userId = 1;
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        userService.create(user);
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Desc 1");
        ItemRequestsResponseDto itemRequestsExpected = new ItemRequestsResponseDto(1, "Desc 1",
                requestDto.getCreated(), new ArrayList<>());

        ItemRequestsResponseDto itemRequestsActual = requestService.saveItemRequest(userId, requestDto);

        assertEquals(1, requestJpa.findAll().size());
        assertEquals(itemRequestsExpected.getId(), itemRequestsActual.getId());
        assertEquals(itemRequestsExpected.getDescription(), itemRequestsActual.getDescription());
        assertEquals(itemRequestsExpected.getCreated(), itemRequestsActual.getCreated());
        assertEquals(itemRequestsExpected.getItems().size(), itemRequestsActual.getItems().size());
    }

    @Test
    @DirtiesContext
    void getRequestsToUserShouldBeOkWithTwoRequest() {
        Integer user1Id = 1;
        Integer user2Id = 2;
        UserDto user1 = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto user2 = new UserDto(null, "User 2", "user2@yandex.ru");
        userService.create(user1);
        userService.create(user2);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Desc 1");
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Desc 2");
        ItemRequestDto requestDto3 = new ItemRequestDto();
        requestDto3.setDescription("Desc 3");
        Integer requestId = 3;
        ItemDto itemDto = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, requestId);
        requestService.saveItemRequest(user1Id, requestDto1);
        requestService.saveItemRequest(user2Id, requestDto2);
        requestService.saveItemRequest(user1Id, requestDto3);
        itemService.saveItem(itemDto, user1Id);
        List<ItemToRequestResponse> itemsResponseRequest3 = new ArrayList<>();
        ItemToRequestResponse itemResponse = new ItemToRequestResponse(1, "Item 1",
                "Desc 1", true, requestId);
        itemsResponseRequest3.add(itemResponse);
        ItemRequestsResponseDto requestResponseDto1 = new ItemRequestsResponseDto(3, "Desc 3",
                requestDto1.getCreated(), itemsResponseRequest3);
        ItemRequestsResponseDto requestResponseDto2 = new ItemRequestsResponseDto(1, "Desc 1",
                requestDto3.getCreated(), new ArrayList<>());

        List<ItemRequestsResponseDto> requestsActual = requestService.getRequestsToUser(user1Id);

        assertEquals(3, requestJpa.findAll().size());
        assertEquals(2, requestsActual.size());
        assertEquals(requestResponseDto1.getId(), requestsActual.get(1).getId());
        assertEquals(requestResponseDto1.getDescription(), requestsActual.get(1).getDescription());
        assertEquals(requestResponseDto1.getCreated(), requestsActual.get(1).getCreated());
        assertEquals(requestResponseDto1.getItems().size(), requestsActual.get(1).getItems().size());
        assertEquals(requestResponseDto2.getId(), requestsActual.get(0).getId());
        assertEquals(requestResponseDto2.getDescription(), requestsActual.get(0).getDescription());
        assertEquals(requestResponseDto2.getCreated(), requestsActual.get(0).getCreated());
        assertEquals(requestResponseDto2.getItems().size(), requestsActual.get(0).getItems().size());
    }

    @Test
    @DirtiesContext
    void getRequestsToUserShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer user1Id = 1;
        Integer user2Id = 2;
        Integer user3Id = 3;
        UserDto user1 = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto user2 = new UserDto(null, "User 2", "user2@yandex.ru");
        userService.create(user1);
        userService.create(user2);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Desc 1");
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Desc 2");
        ItemRequestDto requestDto3 = new ItemRequestDto();
        requestDto3.setDescription("Desc 3");
        Integer requestId = 3;
        ItemDto itemDto = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, requestId);
        requestService.saveItemRequest(user1Id, requestDto1);
        requestService.saveItemRequest(user2Id, requestDto2);
        requestService.saveItemRequest(user1Id, requestDto3);
        itemService.saveItem(itemDto, user1Id);

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> requestService.getRequestsToUser(user3Id));
        assertEquals("User with id = " + user3Id + " not found", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getRequestsToOtherUsersShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer user1Id = 1;
        Integer user2Id = 2;
        Integer user3Id = 3;
        UserDto user1 = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto user2 = new UserDto(null, "User 2", "user2@yandex.ru");
        userService.create(user1);
        userService.create(user2);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Desc 1");
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Desc 2");
        ItemRequestDto requestDto3 = new ItemRequestDto();
        requestDto3.setDescription("Desc 3");
        Integer requestId = 3;
        ItemDto itemDto = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, requestId);
        requestService.saveItemRequest(user1Id, requestDto1);
        requestService.saveItemRequest(user1Id, requestDto2);
        requestService.saveItemRequest(user2Id, requestDto3);
        itemService.saveItem(itemDto, user1Id);

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> requestService.getRequestsToUser(user3Id));
        assertEquals("User with id = " + user3Id + " not found", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getRequestsToOtherUsersShouldBeOk() {
        Integer user1Id = 1;
        Integer user2Id = 2;
        Integer user3Id = 3;
        UserDto user1 = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto user2 = new UserDto(null, "User 2", "user2@yandex.ru");
        UserDto user3 = new UserDto(null, "User 3", "user3@yandex.ru");
        userService.create(user1);
        userService.create(user2);
        userService.create(user3);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Desc 1");
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Desc 2");
        ItemRequestDto requestDto3 = new ItemRequestDto();
        requestDto3.setDescription("Desc 3");
        Integer requestId = 3;
        ItemDto itemDto = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, requestId);
        requestService.saveItemRequest(user1Id, requestDto1);
        requestService.saveItemRequest(user3Id, requestDto2);
        requestService.saveItemRequest(user2Id, requestDto3);
        itemService.saveItem(itemDto, user1Id);
        List<ItemToRequestResponse> itemsResponseRequest3 = new ArrayList<>();
        ItemToRequestResponse itemResponse = new ItemToRequestResponse(1, "Item 1",
                "Desc 1", true, requestId);
        itemsResponseRequest3.add(itemResponse);
        ItemRequestsResponseDto requestResponseDto1 = new ItemRequestsResponseDto(3, "Desc 3",
                requestDto1.getCreated(), itemsResponseRequest3);
        ItemRequestsResponseDto requestResponseDto3 = new ItemRequestsResponseDto(1, "Desc 1",
                requestDto3.getCreated(), new ArrayList<>());

        List<ItemRequestsResponseDto> requestsActual = requestService
                .getRequestsToOtherUsers(user3Id, 0, 20);

        assertEquals(3, requestJpa.findAll().size());
        assertEquals(2, requestsActual.size());
        assertEquals(requestResponseDto1.getId(), requestsActual.get(1).getId());
        assertEquals(requestResponseDto1.getDescription(), requestsActual.get(1).getDescription());
        assertEquals(requestResponseDto1.getCreated(), requestsActual.get(1).getCreated());
        assertEquals(requestResponseDto1.getItems().size(), requestsActual.get(1).getItems().size());
        assertEquals(requestResponseDto3.getId(), requestsActual.get(0).getId());
        assertEquals(requestResponseDto3.getDescription(), requestsActual.get(0).getDescription());
        assertEquals(requestResponseDto3.getCreated(), requestsActual.get(0).getCreated());
        assertEquals(requestResponseDto3.getItems().size(), requestsActual.get(0).getItems().size());
    }

    @Test
    @DirtiesContext
    void getRequestsToOtherUsersTestSize() {
        Integer user1Id = 1;
        Integer user2Id = 2;
        Integer user3Id = 3;
        UserDto user1 = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto user2 = new UserDto(null, "User 2", "user2@yandex.ru");
        UserDto user3 = new UserDto(null, "ПUser 3", "user3@yandex.ru");
        userService.create(user1);
        userService.create(user2);
        userService.create(user3);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Desc 1");
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Desc 2");
        ItemRequestDto requestDto3 = new ItemRequestDto();
        requestDto3.setDescription("Desc 3");
        Integer requestId = 3;
        ItemDto itemDto = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, requestId);
        requestService.saveItemRequest(user1Id, requestDto1);
        requestService.saveItemRequest(user3Id, requestDto2);
        requestService.saveItemRequest(user2Id, requestDto3);
        itemService.saveItem(itemDto, user1Id);
        List<ItemToRequestResponse> itemsResponseRequest3 = new ArrayList<>();
        ItemToRequestResponse itemResponse = new ItemToRequestResponse(1, "Item 1",
                "Desc 1", true, requestId);
        itemsResponseRequest3.add(itemResponse);
        ItemRequestsResponseDto requestResponseDto1 = new ItemRequestsResponseDto(3, "Desc 3",
                requestDto1.getCreated(), itemsResponseRequest3);

        List<ItemRequestsResponseDto> requestsActual = requestService
                .getRequestsToOtherUsers(user3Id, 1, 1);

        assertEquals(3, requestJpa.findAll().size());
        assertEquals(1, requestsActual.size());
        assertEquals(requestResponseDto1.getId(), requestsActual.get(0).getId());
        assertEquals(requestResponseDto1.getDescription(), requestsActual.get(0).getDescription());
        assertEquals(requestResponseDto1.getCreated(), requestsActual.get(0).getCreated());
        assertEquals(requestResponseDto1.getItems().size(), requestsActual.get(0).getItems().size());
    }

    @Test
    @DirtiesContext
    void getRequestShouldThrowDataNotFoundExceptionWithNonexistentRequest() {
        Integer userId = 1;
        Integer requestId = 3;
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        userService.create(user);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Desc 1");
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Desc 2");
        requestService.saveItemRequest(userId, requestDto1);
        requestService.saveItemRequest(userId, requestDto2);

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> requestService.getRequest(userId, requestId));
        assertEquals("Request with id = " + requestId + " not found", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getRequestShouldBeOk() {
        Integer userId = 1;
        Integer requestId = 2;
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        userService.create(user);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Desc 1");
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Desc 2");
        requestService.saveItemRequest(userId, requestDto1);
        requestService.saveItemRequest(userId, requestDto2);
        ItemRequestsResponseDto itemRequestsExpected = new ItemRequestsResponseDto(2, "Desc 2",
                requestDto2.getCreated(), new ArrayList<>());

        ItemRequestsResponseDto itemRequestsActual = requestService.getRequest(userId, requestId);

        assertEquals(itemRequestsExpected.getId(), itemRequestsActual.getId());
        assertEquals(itemRequestsExpected.getDescription(), itemRequestsActual.getDescription());
        assertEquals(itemRequestsExpected.getCreated(), itemRequestsActual.getCreated());
        assertEquals(itemRequestsExpected.getItems().size(), itemRequestsActual.getItems().size());
    }
}
