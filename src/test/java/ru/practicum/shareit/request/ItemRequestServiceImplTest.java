package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapperImpl;
import ru.practicum.shareit.item.dto.ItemToRequestResponse;
import ru.practicum.shareit.item.storage.db.JpaItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestsResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.JpaItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapperImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    private ItemRequestService requestService;
    @Mock
    private JpaItemRequestRepository itemRequestRepository;
    @Mock
    private JpaItemRepository itemRepository;
    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        requestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userService,
                new ItemRequestMapperImpl(), new ItemMapperImpl(), new UserMapperImpl());
    }

    @Test
    void saveItemRequestShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer id = 1;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Desc 1");

        when(userService.getData(anyInt()))
                .thenThrow(new DataNotFoundException("User with id = " + id + " not found"));
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> requestService.saveItemRequest(id, requestDto));

        assertEquals("User with id = " + id + " not found", ex.getMessage());
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void saveItemRequestShouldBeOk() {
        Integer id = 1;
        UserDto userDto = new UserDto(1, "User 1", "user1@yandex.ru");
        User user = new User(1, "User 1", "user1@yandex.ru");
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Desc 1");
        LocalDateTime created = requestDto.getCreated();
        ItemRequest requestSave = new ItemRequest(1, "Desc 1", user, created);
        ItemRequestsResponseDto requestsExpected = new ItemRequestsResponseDto(1, "Desc 1",
                created, new ArrayList<>());

        when(userService.getData(anyInt())).thenReturn(userDto);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(requestSave);

        ItemRequestsResponseDto requestsActual = requestService.saveItemRequest(id, requestDto);
        assertEquals(requestsExpected.getId(), requestsActual.getId());
        assertEquals(requestsExpected.getDescription(), requestsActual.getDescription());
        assertEquals(requestsExpected.getCreated(), requestsActual.getCreated());
        assertEquals(requestsExpected.getItems(), requestsActual.getItems());
        verify(itemRequestRepository, atLeast(1)).save(any(ItemRequest.class));
    }

    @Test
    void getRequestsToUserShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer userId = 1;

        when(userService.getData(anyInt()))
                .thenThrow(new DataNotFoundException("User with id = " + userId + " not found"));
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> requestService.getRequestsToUser(userId));

        assertEquals("User with id = " + userId + " not found", ex.getMessage());
    }

    @Test
    void getRequestsToUserShouldBeOkWithNullItems() {
        Integer userId = 1;
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime created2 = LocalDateTime.now();
        UserDto userDto = new UserDto(1, "User 1", "user1@yandex.ru");
        User user = new User(1, "User 1", "user1@yandex.ru");
        ItemRequest request1 = new ItemRequest(1, "Desc 1", user, created1);
        ItemRequest request2 = new ItemRequest(2, "Desc 2", user, created2);
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);

        when(userService.getData(anyInt())).thenReturn(userDto);
        when(itemRequestRepository.findItemRequestByRequestor_IdOrderByCreatedDesc(anyInt())).thenReturn(requests);
        when(itemRepository.findByRequest_IdIn(anyList())).thenReturn(new ArrayList<>());
        ItemRequestsResponseDto requestResponseDto1 = new ItemRequestsResponseDto(1, "Desc 1",
                created1, new ArrayList<>());
        ItemRequestsResponseDto requestResponseDto2 = new ItemRequestsResponseDto(2, "Desc 2",
                created2, new ArrayList<>());
        List<ItemRequestsResponseDto> requestsResponseDto = requestService.getRequestsToUser(userId);

        assertEquals(2, requestsResponseDto.size());
        assertEquals(requestResponseDto1.getId(), requestsResponseDto.get(0).getId());
        assertEquals(requestResponseDto1.getDescription(), requestsResponseDto.get(0).getDescription());
        assertEquals(requestResponseDto1.getCreated(), requestsResponseDto.get(0).getCreated());
        assertEquals(requestResponseDto1.getItems(), requestsResponseDto.get(0).getItems());
        assertEquals(requestResponseDto2.getId(), requestsResponseDto.get(1).getId());
        assertEquals(requestResponseDto2.getDescription(), requestsResponseDto.get(1).getDescription());
        assertEquals(requestResponseDto2.getCreated(), requestsResponseDto.get(1).getCreated());
        assertEquals(requestResponseDto2.getItems(), requestsResponseDto.get(1).getItems());
    }

    @Test
    void getRequestsToUserShouldBeOk() {
        Integer userId = 1;
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime created2 = LocalDateTime.now();
        UserDto userDto = new UserDto(1, "User 1", "user1@yandex.ru");
        User user = new User(1, "User 1", "user1@yandex.ru");
        ItemRequest request1 = new ItemRequest(1, "Desc 1", user, created1);
        ItemRequest request2 = new ItemRequest(2, "Desc 2", user, created2);
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);
        Item item = new Item(1, "Item 1", "Desc 1", true,
                user, request1);
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemToRequestResponse> itemsResponse = new ArrayList<>();
        ItemToRequestResponse itemResponse = new ItemToRequestResponse(1, "Item 1",
                "Desc 1", true, 1);
        itemsResponse.add(itemResponse);

        when(userService.getData(anyInt())).thenReturn(userDto);
        when(itemRequestRepository.findItemRequestByRequestor_IdOrderByCreatedDesc(anyInt())).thenReturn(requests);
        when(itemRepository.findByRequest_IdIn(anyList())).thenReturn(items);
        ItemRequestsResponseDto requestResponseDto1 = new ItemRequestsResponseDto(1, "Desc 1",
                created1, itemsResponse);
        ItemRequestsResponseDto requestResponseDto2 = new ItemRequestsResponseDto(2, "Desc 2",
                created2, new ArrayList<>());
        List<ItemRequestsResponseDto> requestsResponseDto = requestService.getRequestsToUser(userId);

        assertEquals(2, requestsResponseDto.size());
        assertEquals(requestResponseDto1.getId(), requestsResponseDto.get(0).getId());
        assertEquals(requestResponseDto1.getDescription(), requestsResponseDto.get(0).getDescription());
        assertEquals(requestResponseDto1.getCreated(), requestsResponseDto.get(0).getCreated());
        assertEquals(1, requestsResponseDto.get(0).getItems().size());
        assertEquals(requestResponseDto1.getItems().get(0).getId(),
                requestsResponseDto.get(0).getItems().get(0).getId());
        assertEquals(requestResponseDto1.getItems().get(0).getName(),
                requestsResponseDto.get(0).getItems().get(0).getName());
        assertEquals(requestResponseDto1.getItems().get(0).getDescription(),
                requestsResponseDto.get(0).getItems().get(0).getDescription());
        assertEquals(requestResponseDto1.getItems().get(0).getAvailable(),
                requestsResponseDto.get(0).getItems().get(0).getAvailable());
        assertEquals(requestResponseDto1.getItems().get(0).getRequestId(),
                requestsResponseDto.get(0).getItems().get(0).getRequestId());
        assertEquals(requestResponseDto2.getId(), requestsResponseDto.get(1).getId());
        assertEquals(requestResponseDto2.getDescription(), requestsResponseDto.get(1).getDescription());
        assertEquals(requestResponseDto2.getCreated(), requestsResponseDto.get(1).getCreated());
        assertEquals(requestResponseDto2.getItems(), requestsResponseDto.get(1).getItems());
    }

    @Test
    void getRequestsToOtherUsersShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer userId = 1;

        when(userService.getData(anyInt()))
                .thenThrow(new DataNotFoundException("User with id = " + userId + " not found"));
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> requestService.getRequestsToOtherUsers(1, 0, 20));

        assertEquals("User with id = " + userId + " not found", ex.getMessage());
    }

    @Test
    void getRequestsToOtherUsersShouldBeOkWithNotItems() {
        Integer userId = 1;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        User user3 = new User(3, "User 3", "user3@yandex.ru");
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime created2 = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1, "Desc 1", user2, created1);
        ItemRequest request2 = new ItemRequest(2, "Desc 2", user3, created2);
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);

        when(userService.getData(userId)).thenReturn(user1Dto);
        when(itemRequestRepository.findItemRequestNotByRequestor_IdOrderByCreatedDesc(anyInt())).thenReturn(requests);
        ItemRequestsResponseDto requestResponseDto1 = new ItemRequestsResponseDto(1, "Desc 1",
                created1, new ArrayList<>());
        ItemRequestsResponseDto requestResponseDto2 = new ItemRequestsResponseDto(2, "Desc 2",
                created2, new ArrayList<>());
        List<ItemRequestsResponseDto> requestsResponseDto = requestService.getRequestsToOtherUsers(userId,
                0, 20);

        assertEquals(2, requestsResponseDto.size());
        assertEquals(requestResponseDto1.getId(), requestsResponseDto.get(0).getId());
        assertEquals(requestResponseDto1.getDescription(), requestsResponseDto.get(0).getDescription());
        assertEquals(requestResponseDto1.getCreated(), requestsResponseDto.get(0).getCreated());
        assertEquals(0, requestsResponseDto.get(0).getItems().size());
        assertEquals(requestResponseDto2.getId(), requestsResponseDto.get(1).getId());
        assertEquals(requestResponseDto2.getDescription(), requestsResponseDto.get(1).getDescription());
        assertEquals(requestResponseDto2.getCreated(), requestsResponseDto.get(1).getCreated());
        assertEquals(0, requestsResponseDto.get(1).getItems().size());
    }

    @Test
    void getRequestsToOtherUsersShouldBeOkWithOneItems() {
        Integer userId = 1;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        User user3 = new User(3, "User 3", "user3@yandex.ru");
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime created2 = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1, "Desc 1", user2, created1);
        ItemRequest request2 = new ItemRequest(2, "Desc 2", user3, created2);
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);
        Item item = new Item(1, "Item 1", "Desc 1", true,
                user1, request2);
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemToRequestResponse> itemsResponse = new ArrayList<>();
        ItemToRequestResponse itemResponse = new ItemToRequestResponse(1, "Item 1",
                "Desc 1", true, 2);
        itemsResponse.add(itemResponse);

        when(userService.getData(userId)).thenReturn(user1Dto);
        when(itemRequestRepository.findItemRequestNotByRequestor_IdOrderByCreatedDesc(anyInt())).thenReturn(requests);
        when(itemRepository.findByRequest_IdIn(anyList())).thenReturn(items);
        ItemRequestsResponseDto requestResponseDto1 = new ItemRequestsResponseDto(1, "Desc 1",
                created1, new ArrayList<>());
        ItemRequestsResponseDto requestResponseDto2 = new ItemRequestsResponseDto(2, "Desc 2",
                created2, itemsResponse);
        List<ItemRequestsResponseDto> requestsResponseDto = requestService.getRequestsToOtherUsers(userId,
                0, 20);

        assertEquals(2, requestsResponseDto.size());
        assertEquals(requestResponseDto1.getId(), requestsResponseDto.get(0).getId());
        assertEquals(requestResponseDto1.getDescription(), requestsResponseDto.get(0).getDescription());
        assertEquals(requestResponseDto1.getCreated(), requestsResponseDto.get(0).getCreated());
        assertEquals(0, requestsResponseDto.get(0).getItems().size());
        assertEquals(requestResponseDto2.getId(), requestsResponseDto.get(1).getId());
        assertEquals(requestResponseDto2.getDescription(), requestsResponseDto.get(1).getDescription());
        assertEquals(requestResponseDto2.getCreated(), requestsResponseDto.get(1).getCreated());
        assertEquals(1, requestsResponseDto.get(1).getItems().size());
        assertEquals(requestResponseDto2.getItems().get(0).getId(),
                requestsResponseDto.get(1).getItems().get(0).getId());
        assertEquals(requestResponseDto2.getItems().get(0).getName(),
                requestsResponseDto.get(1).getItems().get(0).getName());
        assertEquals(requestResponseDto2.getItems().get(0).getDescription(),
                requestsResponseDto.get(1).getItems().get(0).getDescription());
        assertEquals(requestResponseDto2.getItems().get(0).getAvailable(),
                requestsResponseDto.get(1).getItems().get(0).getAvailable());
        assertEquals(requestResponseDto2.getItems().get(0).getRequestId(),
                requestsResponseDto.get(1).getItems().get(0).getRequestId());
    }

    @Test
    void getRequestShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer requestId = 1;
        Integer userId = 2;

        when(userService.getData(anyInt()))
                .thenThrow(new DataNotFoundException("User with id = " + userId + " not found"));
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> requestService.getRequest(userId, requestId));

        assertEquals("User with id = " + userId + " not found", ex.getMessage());
    }

    @Test
    void getRequestShouldThrowDataNotFoundExceptionWithNonexistentRequest() {
        Integer requestId = 1;
        Integer userId = 2;
        UserDto user = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user);
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.empty());
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> requestService.getRequest(userId, requestId));

        assertEquals("Request with id = " + requestId + " not found", ex.getMessage());
    }

    @Test
    void getRequestShouldBeOkWithNullItems() {
        Integer requestId = 1;
        Integer userId = 2;
        LocalDateTime created = LocalDateTime.now();
        User user = new User(1, "User 1", "user1@yandex.ru");
        UserDto userDto = new UserDto(1, "User 1", "user1@yandex.ru");
        ItemRequest request = new ItemRequest(1, "Desc 1", user, created);
        ItemRequestsResponseDto requestsExpected = new ItemRequestsResponseDto(1, "Desc 1",
                created, new ArrayList<>());

        when(userService.getData(anyInt())).thenReturn(userDto);
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(request));
        ItemRequestsResponseDto requestsActual = requestService.getRequest(userId, requestId);

        assertEquals(requestsExpected.getId(), requestsActual.getId());
        assertEquals(requestsExpected.getDescription(), requestsActual.getDescription());
        assertEquals(requestsExpected.getCreated(), requestsActual.getCreated());
        assertEquals(requestsExpected.getItems(), requestsActual.getItems());
    }

    @Test
    void getRequestShouldBeOkWithOneItem() {
        Integer requestId = 1;
        Integer userId = 2;
        LocalDateTime created = LocalDateTime.now();
        User user = new User(2, "User 2", "user2@yandex.ru");
        UserDto userDto = new UserDto(2, "User 2", "user2@yandex.ru");
        ItemRequest request = new ItemRequest(1, "Desc 1", user, created);
        Item item = new Item(1, "Item 1", "Desc 1", true,
                user, request);
        List<Item> items = new ArrayList<>();
        items.add(item);

        when(userService.getData(anyInt())).thenReturn(userDto);
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(request));
        when(itemRepository.findByRequest_Id(anyInt())).thenReturn(items);
        List<ItemToRequestResponse> itemsResponse = new ArrayList<>();
        ItemToRequestResponse itemResponse = new ItemToRequestResponse(1, "Item 1",
                "Desc 1", true, requestId);
        itemsResponse.add(itemResponse);
        ItemRequestsResponseDto requestsExpected = new ItemRequestsResponseDto(1, "Desc 1",
                created, itemsResponse);
        ItemRequestsResponseDto requestsActual = requestService.getRequest(userId, requestId);

        assertEquals(requestsExpected.getId(), requestsActual.getId());
        assertEquals(requestsExpected.getDescription(), requestsActual.getDescription());
        assertEquals(requestsExpected.getCreated(), requestsActual.getCreated());
        assertEquals(1, requestsActual.getItems().size());
        assertEquals(requestsExpected.getItems().get(0).getId(), requestsActual.getItems().get(0).getId());
        assertEquals(requestsExpected.getItems().get(0).getName(), requestsActual.getItems().get(0).getName());
        assertEquals(requestsExpected.getItems().get(0).getDescription(),
                requestsActual.getItems().get(0).getDescription());
        assertEquals(requestsExpected.getItems().get(0).getAvailable(),
                requestsActual.getItems().get(0).getAvailable());
        assertEquals(requestsExpected.getItems().get(0).getRequestId(),
                requestsActual.getItems().get(0).getRequestId());
    }
}
