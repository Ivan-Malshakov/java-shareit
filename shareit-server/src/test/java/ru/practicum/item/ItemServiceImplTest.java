package ru.practicum.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingMapperImpl;
import ru.practicum.booking.BookingStatus;
import ru.practicum.booking.dto.BookingResponseToItemDto;
import ru.practicum.booking.storage.JpaBookingRepository;
import ru.practicum.exception.exceptions.BookingNotFoundException;
import ru.practicum.exception.exceptions.DataNotFoundException;
import ru.practicum.exception.exceptions.ForbiddenUpdateException;
import ru.practicum.item.dto.CommentResearchDto;
import ru.practicum.item.dto.CommentResponseDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.service.ItemService;
import ru.practicum.item.service.ItemServiceImpl;
import ru.practicum.item.storage.db.JpaCommentRepository;
import ru.practicum.item.storage.db.JpaItemRepository;
import ru.practicum.request.ItemRequest;
import ru.practicum.request.storage.JpaItemRequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserMapperImpl;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    private ItemService itemService;
    @Mock
    private JpaItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private JpaBookingRepository bookingRepository;
    @Mock
    private JpaItemRequestRepository itemRequest;
    @Mock
    private JpaCommentRepository commentRepository;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(itemRepository, commentRepository, bookingRepository,
                itemRequest, new ItemMapperImpl(), new BookingMapperImpl(), new CommentMapperImpl(),
                new UserMapperImpl(), userService);
    }

    @Test
    void getItemShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer itemId = 1;
        Integer userId = 1;

        when(userService.getData(anyInt())).thenThrow(
                new DataNotFoundException("User with id = " + userId + " not found"));
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.getItem(itemId, userId));

        assertEquals("User with id = " + userId + " not found", ex.getMessage());
    }

    @Test
    void getItemShouldThrowDataNotFoundExceptionWithNonexistentItem() {
        Integer itemId = 1;
        Integer userId = 1;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.getItem(itemId, userId));

        assertEquals("Item with id = " + itemId + " not found", ex.getMessage());
    }

    @Test
    void getItemShouldBeOkWithoutOwnerAndComment() {
        Integer itemId = 1;
        Integer userId = 2;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        Optional<Item> itemOptional = Optional.of(new Item(1, "Item 1", "Desc 1",
                true, user1, null));
        List<Comment> comments = new ArrayList<>();

        when(userService.getData(anyInt())).thenReturn(user1Dto);
        when(itemRepository.findById(anyInt())).thenReturn(itemOptional);
        when(commentRepository.findCommentByItem_Id(anyInt())).thenReturn(comments);
        ItemDto itemExpected = new ItemDto(1, "Item 1", "Desc 1",
                true, null, null, new ArrayList<>(), null);
        ItemDto itemActual = itemService.getItem(itemId, userId);

        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getNextBooking(), itemActual.getNextBooking());
        assertEquals(itemExpected.getLastBooking(), itemActual.getLastBooking());
        assertEquals(itemExpected.getComments(), itemActual.getComments());
        assertEquals(itemExpected.getRequestId(), itemActual.getRequestId());
    }

    @Test
    void getItemShouldBeOkWithoutOwner() {
        Integer itemId = 1;
        Integer userId = 2;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        Optional<Item> itemOptional = Optional.of(new Item(1, "Item 1", "Desc 1",
                true, user1, null));
        Item item = itemOptional.get();
        LocalDateTime created = LocalDateTime.now();
        Comment comment = new Comment(1, "Comment 1", user1, item, created);
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        List<CommentResponseDto> commentsResponseDto = new ArrayList<>();
        CommentResponseDto commentResponseDto = new CommentResponseDto(1, "Comment 1",
                "User 1", created);
        commentsResponseDto.add(commentResponseDto);

        when(userService.getData(anyInt())).thenReturn(user1Dto);
        when(itemRepository.findById(anyInt())).thenReturn(itemOptional);
        when(commentRepository.findCommentByItem_Id(anyInt())).thenReturn(comments);
        ItemDto itemExpected = new ItemDto(1, "Item 1", "Desc 1",
                true, null, null, commentsResponseDto, null);
        ItemDto itemActual = itemService.getItem(itemId, userId);

        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getNextBooking(), itemActual.getNextBooking());
        assertEquals(itemExpected.getLastBooking(), itemActual.getLastBooking());
        assertEquals(itemExpected.getComments(), itemActual.getComments());
        assertEquals(itemExpected.getRequestId(), itemActual.getRequestId());
    }

    @Test
    void getItemShouldBeOkWithoutCommentsAndBookings() {
        Integer itemId = 1;
        Integer userId = 1;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        Optional<Item> itemOptional = Optional.of(new Item(1, "Item 1", "Desc 1",
                true, user1, null));

        when(userService.getData(anyInt())).thenReturn(user1Dto);
        when(itemRepository.findById(anyInt())).thenReturn(itemOptional);
        when(bookingRepository.findBookingByItemAndStartAfter(anyInt(), any(BookingStatus.class)))
                .thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingByItemAndStartBefore(anyInt(), any(BookingStatus.class)))
                .thenReturn(new ArrayList<>());
        ItemDto itemExpected = new ItemDto(1, "Item 1", "Desc 1",
                true, null, null, new ArrayList<>(), null);
        ItemDto itemActual = itemService.getItem(itemId, userId);

        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getNextBooking(), itemActual.getNextBooking());
        assertEquals(itemExpected.getLastBooking(), itemActual.getLastBooking());
        assertEquals(itemExpected.getComments(), itemActual.getComments());
        assertEquals(itemExpected.getRequestId(), itemActual.getRequestId());
    }

    @Test
    void getItemShouldBeOkWithoutCommentsAndNextBooking() {
        Integer itemId = 1;
        Integer userId = 1;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        Optional<Item> itemOptional = Optional.of(new Item(1, "Item 1", "Desc 1",
                true, user1, null));
        Item item = itemOptional.get();
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now().minusDays(15);
        Booking bookingLast = new Booking(1, start, end, item, user1, BookingStatus.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingLast);

        when(userService.getData(anyInt())).thenReturn(user1Dto);
        when(itemRepository.findById(anyInt())).thenReturn(itemOptional);
        when(bookingRepository.findBookingByItemAndStartAfter(anyInt(), any(BookingStatus.class)))
                .thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingByItemAndStartBefore(anyInt(), any(BookingStatus.class)))
                .thenReturn(bookings);
        BookingResponseToItemDto bookingLastResponse = new BookingResponseToItemDto(1,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(end),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(start), 1);
        ItemDto itemExpected = new ItemDto(1, "Item 1", "Desc 1",
                true, null, bookingLastResponse, new ArrayList<>(), null);
        ItemDto itemActual = itemService.getItem(itemId, userId);

        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getNextBooking(), itemActual.getNextBooking());
        assertEquals(itemExpected.getLastBooking(), itemActual.getLastBooking());
        assertEquals(itemExpected.getComments(), itemActual.getComments());
        assertEquals(itemExpected.getRequestId(), itemActual.getRequestId());
    }

    @Test
    void getItemShouldBeOkWithoutCommentsAndLastBooking() {
        Integer itemId = 1;
        Integer userId = 1;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        Optional<Item> itemOptional = Optional.of(new Item(1, "Item 1", "Desc 1",
                true, user1, null));
        Item item = itemOptional.get();
        LocalDateTime startNext = LocalDateTime.now().plusDays(30);
        LocalDateTime endNext = LocalDateTime.now().plusDays(90);
        Booking bookingNext = new Booking(1, startNext, endNext, item, user1, BookingStatus.APPROVED);
        List<Booking> bookingsNext = new ArrayList<>();
        bookingsNext.add(bookingNext);
        LocalDateTime startEnd = LocalDateTime.now().minusDays(30);
        LocalDateTime endEnd = LocalDateTime.now().minusDays(15);
        Booking bookingLast = new Booking(1, startEnd, endEnd, item, user1, BookingStatus.APPROVED);
        List<Booking> bookingsLast = new ArrayList<>();
        bookingsLast.add(bookingLast);

        when(userService.getData(anyInt())).thenReturn(user1Dto);
        when(itemRepository.findById(anyInt())).thenReturn(itemOptional);
        when(bookingRepository.findBookingByItemAndStartAfter(anyInt(), any(BookingStatus.class)))
                .thenReturn(bookingsNext);
        when(bookingRepository.findBookingByItemAndStartBefore(anyInt(), any(BookingStatus.class)))
                .thenReturn(bookingsLast);
        BookingResponseToItemDto bookingNextResponse = new BookingResponseToItemDto(1,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(endNext),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(startNext), 1);
        BookingResponseToItemDto bookingLastResponse = new BookingResponseToItemDto(1,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(endEnd),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(startEnd), 1);
        ItemDto itemExpected = new ItemDto(1, "Item 1", "Desc 1",
                true, bookingNextResponse, bookingLastResponse, new ArrayList<>(), null);
        ItemDto itemActual = itemService.getItem(itemId, userId);

        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getNextBooking(), itemActual.getNextBooking());
        assertEquals(itemExpected.getLastBooking(), itemActual.getLastBooking());
        assertEquals(itemExpected.getComments(), itemActual.getComments());
        assertEquals(itemExpected.getRequestId(), itemActual.getRequestId());
    }

    @Test
    void getItemShouldBeOk() {
        Integer itemId = 1;
        Integer userId = 1;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        Optional<Item> itemOptional = Optional.of(new Item(1, "Item 1", "Desc 1",
                true, user1, null));
        Item item = itemOptional.get();
        LocalDateTime start = LocalDateTime.now().plusDays(30);
        LocalDateTime end = LocalDateTime.now().plusDays(90);
        Booking bookingNext = new Booking(1, start, end, item, user1, BookingStatus.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingNext);
        LocalDateTime created = LocalDateTime.now();
        Comment comment = new Comment(1, "Comment 1", user1, item, created);
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        List<CommentResponseDto> commentsResponseDto = new ArrayList<>();
        CommentResponseDto commentResponseDto = new CommentResponseDto(1, "Comment 1",
                "User 1", created);
        commentsResponseDto.add(commentResponseDto);

        when(userService.getData(anyInt())).thenReturn(user1Dto);
        when(itemRepository.findById(anyInt())).thenReturn(itemOptional);
        when(bookingRepository.findBookingByItemAndStartAfter(anyInt(), any(BookingStatus.class)))
                .thenReturn(bookings);
        when(bookingRepository.findBookingByItemAndStartBefore(anyInt(), any(BookingStatus.class)))
                .thenReturn(new ArrayList<>());
        when(commentRepository.findCommentByItem_Id(anyInt())).thenReturn(comments);
        BookingResponseToItemDto bookingNextResponse = new BookingResponseToItemDto(1,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(end),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(start), 1);
        ItemDto itemExpected = new ItemDto(1, "Item 1", "Desc 1",
                true, bookingNextResponse, null, commentsResponseDto, null);
        ItemDto itemActual = itemService.getItem(itemId, userId);

        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getNextBooking(), itemActual.getNextBooking());
        assertEquals(itemExpected.getLastBooking(), itemActual.getLastBooking());
        assertEquals(itemExpected.getComments(), itemActual.getComments());
        assertEquals(itemExpected.getRequestId(), itemActual.getRequestId());
    }

    @Test
    void getItemToUserShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer userId = 1;

        when(userService.getData(anyInt())).thenThrow(
                new DataNotFoundException("Item with id = " + userId + " not found"));
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.getItemToUser(anyList().size()));

        assertEquals("Item with id = " + userId + " not found", ex.getMessage());
    }

    @Test
    void getItemToUserShouldBeOk() {
        Integer userId = 1;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(itemRepository.findByOwnerIdOrderByIdAsc(anyInt())).thenReturn(new ArrayList<>());
        List<ItemDto> itemsActual = itemService.getItemToUser(userId);

        assertEquals(0, itemsActual.size());
    }

    @Test
    void updateItemShouldThrowDataNotFoundExceptionWithNonexistentItem() {
        Integer userId = 1;
        Integer itemId = 1;
        ItemDto itemUpdateDto = new ItemDto(null, "New item 1", null,
                null, null, null, null, null);

        when(itemRepository.findById(anyInt())).thenThrow(new DataNotFoundException(
                "Item with id = " + itemId + " not found"));
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.updateItem(itemUpdateDto, userId, itemId));

        assertEquals("Item with id = " + itemId + " not found", ex.getMessage());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItemShouldThrowForbiddenUpdateException() {
        Integer userId = 2;
        Integer itemId = 1;
        ItemDto itemUpdateDto = new ItemDto(null, "New item 1", null,
                null, null, null, null, null);
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        Optional<Item> item = Optional.of(new Item(1, "Item 1", "Desc 1",
                true, user1, null));

        when(itemRepository.findById(anyInt())).thenReturn(item);
        ForbiddenUpdateException ex = assertThrows(ForbiddenUpdateException.class,
                () -> itemService.updateItem(itemUpdateDto, userId, itemId));

        assertEquals("User with id = " + userId + " does not have rights to change item " +
                "because he is not its owner", ex.getMessage());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItemShouldBeOkWithNewName() {
        Integer userId = 1;
        Integer itemId = 1;
        ItemDto itemUpdateDto = new ItemDto(null, "New item 1", null,
                null, null, null, null, null);
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        Optional<Item> item = Optional.of(new Item(1, "Item 1", "Desc 1",
                true, user1, null));
        ItemDto itemExpectedDto = new ItemDto(1, "New item 1", "Desc 1",
                true, null, null, null, null);
        Item itemUpdate = new Item(1, "New item 1", "Desc 1",
                true, user1, null);

        when(itemRepository.findById(anyInt())).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(itemUpdate);

        assertEquals(itemExpectedDto, itemService.updateItem(itemUpdateDto, userId, itemId));
        verify(itemRepository, atLeast(1)).save(any(Item.class));
    }

    @Test
    void updateItemShouldBeOkWithNewDescription() {
        Integer userId = 1;
        Integer itemId = 1;
        ItemDto itemUpdateDto = new ItemDto(null, null, "New desc 1",
                null, null, null, null, null);
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        Optional<Item> item = Optional.of(new Item(1, "Item 1", "Desc 1",
                true, user1, null));
        ItemDto itemExpectedDto = new ItemDto(1, "Item 1", "New desc 1",
                true, null, null, null, null);
        Item itemUpdate = new Item(1, "Item 1", "New desc 1",
                true, user1, null);

        when(itemRepository.findById(anyInt())).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(itemUpdate);

        assertEquals(itemExpectedDto, itemService.updateItem(itemUpdateDto, userId, itemId));
        verify(itemRepository, atLeast(1)).save(any(Item.class));
    }

    @Test
    void updateItemShouldBeOkWithNewAvailable() {
        Integer userId = 1;
        Integer itemId = 1;
        ItemDto itemUpdateDto = new ItemDto(null, null, null,
                false, null, null, null, null);
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        Optional<Item> item = Optional.of(new Item(1, "Item 1", "Desc 1",
                true, user1, null));
        ItemDto itemExpectedDto = new ItemDto(1, "Item 1", "Desc 1",
                false, null, null, null, null);
        Item itemUpdate = new Item(1, "Item 1", "Desc 1",
                false, user1, null);

        when(itemRepository.findById(anyInt())).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(itemUpdate);

        assertEquals(itemExpectedDto, itemService.updateItem(itemUpdateDto, userId, itemId));
        verify(itemRepository, atLeast(1)).save(any(Item.class));
    }

    @Test
    void updateItemShouldBeOkWithNewAllParams() {
        Integer userId = 1;
        Integer itemId = 1;
        ItemDto itemUpdateDto = new ItemDto(null, "New item 1", "New desc 1",
                false, null, null, null, null);
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        Optional<Item> item = Optional.of(new Item(1, "Item 1", "Desc 1",
                true, user1, null));
        ItemDto itemExpectedDto = new ItemDto(1, "New item 1", "New desc 1",
                false, null, null, null, null);
        Item itemUpdate = new Item(1, "New item 1", "New desc 1",
                false, user1, null);

        when(itemRepository.findById(anyInt())).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(itemUpdate);

        assertEquals(itemExpectedDto, itemService.updateItem(itemUpdateDto, userId, itemId));
        verify(itemRepository, atLeast(1)).save(any(Item.class));
    }

    @Test
    void saveItemShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer id = 1;
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, null);

        when(userService.getData(anyInt())).thenThrow(
                new DataNotFoundException("User with id = " + id + " not found"));
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.saveItem(itemDto1, id));

        assertEquals("User with id = " + id + " not found", ex.getMessage());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void saveItemShouldBeOkWithRequestIdNull() {
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, null);
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        Item saveItem = new Item(1, "Item 1", "Desc 1", true,
                user1, null);
        ItemDto itemDtoExpected = new ItemDto(1, "Item 1", "Desc 1", true,
                null, null, null, null);

        when(userService.getData(anyInt())).thenReturn(user1Dto);
        when(itemRepository.save(any(Item.class))).thenReturn(saveItem);

        assertEquals(itemDtoExpected, itemService.saveItem(itemDto1, 1));
        verify(itemRepository, atLeast(1)).save(any(Item.class));
    }

    @Test
    void saveItemShouldBeOkWithRequestIdNotNull() {
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, null);
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        Item saveItem = new Item(1, "Item 1", "Desc 1", true,
                user1, null);
        ItemDto itemDtoExpected = new ItemDto(1, "Item 1", "Desc 1", true,
                null, null, null, null);

        when(userService.getData(anyInt())).thenReturn(user1Dto);
        when(itemRepository.save(any(Item.class))).thenReturn(saveItem);

        assertEquals(itemDtoExpected, itemService.saveItem(itemDto1, 1));
        verify(itemRepository, atLeast(1)).save(any(Item.class));
    }

    @Test
    void saveItemShouldThrowDataNotFoundExceptionWithNonexistentRequest() {
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, 1);

        when(itemRequest.findById(anyInt())).thenReturn(Optional.empty());
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.saveItem(itemDto1, 1));

        assertEquals("Request with id = " + itemDto1.getRequestId() + " not found", ex.getMessage());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void saveItemShouldBeOkWithRequestIdOne() {
        Integer id = 1;
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, 1);
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        ItemRequest request = new ItemRequest(1, "Something", user2, LocalDateTime.now());
        Item saveItem = new Item(1, "Item 1", "Desc 1", true,
                user1, request);

        when(itemRequest.findById(anyInt())).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(saveItem);
        ItemDto itemDtoExpected = new ItemDto(1, "Item 1", "Desc 1", true,
                null, null, null, 1);

        assertEquals(itemDtoExpected, itemService.saveItem(itemDto1, id));
        verify(itemRepository, atLeast(1)).save(any(Item.class));
    }

    @Test
    void searchItemShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer userId = 1;
        String search = "Something";

        when(userService.getData(anyInt())).thenThrow(
                new DataNotFoundException("User with id = " + userId + " not found"));
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.searchItem(search, userId));

        assertEquals("User with id = " + userId + " not found", ex.getMessage());
    }

    @Test
    void searchItemShouldBeOkWithBlankSearch() {
        Integer userId = 1;
        String search = "";

        when(userService.getData(anyInt())).thenReturn(
                new UserDto(1, "User 1", "user1@yandex.ru"));

        assertEquals(0, itemService.searchItem(search, userId).size());
    }

    @Test
    void searchItemShouldSearchOneItem() {
        Integer userId = 1;
        String search = "Item 1";
        List<Item> items = new ArrayList<>();
        items.add(new Item(1, "Item 1", "Desc 1", true,
                new User(2, "User 2", "user2@yandex.ru"), null));

        when(userService.getData(anyInt())).thenReturn(
                new UserDto(1, "User 1", "user1@yandex.ru"));
        when(itemRepository.findByNameAndDescription(anyString(), anyString())).thenReturn(items);

        assertEquals(1, itemService.searchItem(search, userId).size());
    }

    @Test
    void getItemToBookingShouldThrowDataNotFoundExceptionWithNonexistentItem() {
        Integer id = 1;

        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.getItemToBooking(id));

        assertEquals("Item with id = " + id + " not found", ex.getMessage());
    }

    @Test
    void getItemToBookingShouldBeOk() {
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        Optional<Item> itemOptional = Optional.of(new Item(1, "Item 1", "Desc 1",
                true, user1, null));

        when(itemRepository.findById(anyInt())).thenReturn(itemOptional);
        Item itemActual = itemService.getItemToBooking(1);
        Item itemExpected = itemOptional.get();

        assertEquals(itemExpected.getId(), itemActual.getId());
        assertEquals(itemExpected.getName(), itemActual.getName());
        assertEquals(itemExpected.getDescription(), itemActual.getDescription());
        assertEquals(itemExpected.getOwner(), itemActual.getOwner());
        assertEquals(itemExpected.getAvailable(), itemActual.getAvailable());
        assertEquals(itemExpected.getRequest(), itemActual.getRequest());
    }

    @Test
    void saveCommentShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer itemId = 1;
        Integer userId = 1;
        CommentResearchDto comment = new CommentResearchDto("Comment 1", LocalDateTime.now());

        when(userService.getData(anyInt())).thenThrow(
                new DataNotFoundException("User with id = " + userId + " not found"));
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.saveComment(itemId, userId, comment));

        assertEquals("User with id = " + userId + " not found", ex.getMessage());
    }

    @Test
    void saveCommentShouldThrowDataNotFoundExceptionWithNonexistentItem() {
        Integer itemId = 1;
        Integer userId = 1;
        CommentResearchDto comment = new CommentResearchDto("Comment 1", LocalDateTime.now());
        UserDto user = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());
        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.saveComment(itemId, userId, comment));

        assertEquals("Item with id = " + itemId + " not found", ex.getMessage());
    }

    @Test
    void saveCommentShouldThrowBookingNotFoundException() {
        Integer itemId = 1;
        Integer userId = 1;
        CommentResearchDto comment = new CommentResearchDto("Comment 1", LocalDateTime.now());
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        Optional<Item> item = Optional.of(new Item(1, "Item 1", "Desc 1",
                true, user2, null));
        List<Booking> bookings = new ArrayList<>();

        when(userService.getData(anyInt())).thenReturn(user1);
        when(itemRepository.findById(anyInt())).thenReturn(item);
        when(bookingRepository.findBookingByItem_IdAndBooker_IdAndEndBeforeAndStatus(
                anyInt(), anyInt(), any(LocalDateTime.class), any(BookingStatus.class))).thenReturn(bookings);
        BookingNotFoundException ex = assertThrows(BookingNotFoundException.class,
                () -> itemService.saveComment(itemId, userId, comment));

        assertEquals("Completed bookings for a user with id = " +
                userId + " for item with id = " + itemId + " not found", ex.getMessage());
    }

    @Test
    void saveCommentShouldBeOk() {
        Integer itemId = 1;
        Integer userId = 1;
        CommentResearchDto commentDto = new CommentResearchDto("Comment 1", LocalDateTime.now());
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        LocalDateTime created = LocalDateTime.now();
        Optional<Item> itemOptional = Optional.of(new Item(1, "Item 1", "Desc 1",
                true, user2, null));
        Item item = new Item(1, "Item 1", "Desc 1",
                true, user2, null);
        Comment commentSave = new Comment(1, "Comment 1", user1, item, created);
        List<Booking> bookings = new ArrayList<>();
        Booking booking1 = new Booking(1, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(10), item,
                user1, BookingStatus.APPROVED);
        CommentResponseDto commentExpected = new CommentResponseDto(1, "Comment 1",
                "User 1", created);
        bookings.add(booking1);

        when(userService.getData(anyInt())).thenReturn(user1Dto);
        when(itemRepository.findById(anyInt())).thenReturn(itemOptional);
        when(bookingRepository.findBookingByItem_IdAndBooker_IdAndEndBeforeAndStatus(
                anyInt(), anyInt(), any(LocalDateTime.class), any(BookingStatus.class))).thenReturn(bookings);
        when(commentRepository.save(any(Comment.class))).thenReturn(commentSave);
        CommentResponseDto commentActual = itemService.saveComment(itemId, userId, commentDto);

        assertEquals(commentExpected.getId(), commentActual.getId());
        assertEquals(commentExpected.getText(), commentActual.getText());
        assertEquals(commentExpected.getCreated(), commentActual.getCreated());
        assertEquals(commentExpected.getAuthorName(), commentActual.getAuthorName());
        verify(commentRepository, atLeast(1)).save(any(Comment.class));
    }
}
