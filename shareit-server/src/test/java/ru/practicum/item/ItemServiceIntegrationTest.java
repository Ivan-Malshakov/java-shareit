package ru.practicum.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.booking.dto.BookingResearchDto;
import ru.practicum.booking.dto.BookingResponseToItemDto;
import ru.practicum.booking.service.BookingService;
import ru.practicum.exception.exceptions.BookingNotFoundException;
import ru.practicum.exception.exceptions.DataNotFoundException;
import ru.practicum.exception.exceptions.ForbiddenUpdateException;
import ru.practicum.item.dto.CommentResearchDto;
import ru.practicum.item.dto.CommentResponseDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.service.ItemService;
import ru.practicum.item.storage.db.JpaItemRepository;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemServiceIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private JpaItemRepository repository;
    @Autowired
    private BookingService bookingService;

    @Test
    @DirtiesContext
    void getItemShouldBeOk() {
        Integer userId = 1;
        Integer itemId = 1;
        LocalDateTime endLast = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).minusDays(1);
        LocalDateTime startLast = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).minusDays(2);
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null, endLast, startLast, itemId);
        LocalDateTime endNext = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(20);
        LocalDateTime startNext = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(10);
        BookingResearchDto bookingResearchDtoNext = new BookingResearchDto(null, endNext, startNext, itemId);
        UserDto user1 = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto user2 = new UserDto(null, "User 2", "user2@yandex.ru");
        UserDto user3 = new UserDto(null, "User 3", "user3@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, null);
        LocalDateTime createdComment = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        CommentResearchDto commentResearchDto = new CommentResearchDto("Comment 1", createdComment);
        userService.create(user1);
        userService.create(user2);
        userService.create(user3);
        itemService.saveItem(itemDto1, userId);
        bookingService.saveBooking(bookingResearchDto, 2);
        bookingService.approvedOrRejectBooking(userId, 1, true);
        bookingService.saveBooking(bookingResearchDtoNext, 3);
        bookingService.approvedOrRejectBooking(userId, 2, true);
        itemService.saveComment(itemId, 2, commentResearchDto);
        List<CommentResponseDto> commentsResponse = new ArrayList<>();
        CommentResponseDto commentResponse = new CommentResponseDto(1, "Comment 1",
                "User 2", createdComment);
        commentsResponse.add(commentResponse);

        ItemDto itemResponse = itemService.getItem(itemId, userId);
        ItemDto itemResponseExpected = new ItemDto(1, "Item 1", "Desc 1", true,
                new BookingResponseToItemDto(2, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(endNext),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(startNext), 3),
                new BookingResponseToItemDto(1, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(endLast),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(startLast), 2),
                commentsResponse, null);

        assertEquals(itemResponseExpected.getId(), itemResponse.getId());
        assertEquals(itemResponseExpected.getName(), itemResponse.getName());
        assertEquals(itemResponseExpected.getDescription(), itemResponse.getDescription());
        assertEquals(itemResponseExpected.getAvailable(), itemResponse.getAvailable());
        assertEquals(itemResponseExpected.getNextBooking(), itemResponse.getNextBooking());
        assertEquals(itemResponseExpected.getLastBooking(), itemResponse.getLastBooking());
        assertEquals(itemResponseExpected.getComments(), itemResponse.getComments());
        assertEquals(itemResponseExpected.getRequestId(), itemResponse.getRequestId());
    }

    @Test
    @DirtiesContext
    void getItemShouldThrowDataNotFoundExceptionNonexistentUser() {
        Integer userId = 1;
        Integer itemId = 1;

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.getItem(itemId, userId));

        assertEquals("User with id = " + userId + " not found", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getItemToUserShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer userId = 1;

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.getItemToUser(userId));

        assertEquals("User with id = " + userId + " not found", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getItemToUserShouldBeOk() {
        Integer userId = 1;
        Integer itemId = 1;
        LocalDateTime endLast = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).minusDays(1);
        LocalDateTime startLast = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).minusDays(2);
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null, endLast, startLast, itemId);
        LocalDateTime endNext = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(20);
        LocalDateTime startNext = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(10);
        BookingResearchDto bookingResearchDtoNext = new BookingResearchDto(null, endNext, startNext, itemId);
        UserDto user1 = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto user2 = new UserDto(null, "User 2", "user2@yandex.ru");
        UserDto user3 = new UserDto(null, "User 3", "user3@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, null);
        ItemDto itemDto2 = new ItemDto(null, "Item 2", "Desc 2", true,
                null, null, null, null);
        LocalDateTime createdComment = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        CommentResearchDto commentResearchDto = new CommentResearchDto("Comment 1", createdComment);
        userService.create(user1);
        userService.create(user2);
        userService.create(user3);
        itemService.saveItem(itemDto1, userId);
        itemService.saveItem(itemDto2, userId);
        bookingService.saveBooking(bookingResearchDto, 2);
        bookingService.approvedOrRejectBooking(userId, 1, true);
        bookingService.saveBooking(bookingResearchDtoNext, 3);
        bookingService.approvedOrRejectBooking(userId, 2, true);
        itemService.saveComment(itemId, 2, commentResearchDto);
        List<CommentResponseDto> commentsResponse = new ArrayList<>();
        CommentResponseDto commentResponse = new CommentResponseDto(1, "Comment 1",
                "User 2", createdComment);
        commentsResponse.add(commentResponse);

        ItemDto item1ResponseExpected = new ItemDto(1, "Item 1", "Desc 1", true,
                new BookingResponseToItemDto(2, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(endNext),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(startNext), 3),
                new BookingResponseToItemDto(1, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(endLast),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(startLast), 2),
                commentsResponse, null);
        ItemDto item2ResponseExpected = new ItemDto(2, "Item 2", "Desc 2", true,
                null, null, null, null);
        List<ItemDto> itemsActual = itemService.getItemToUser(userId);

        assertEquals(2, itemsActual.size());
        assertEquals(item1ResponseExpected.getId(), itemsActual.get(0).getId());
        assertEquals(item1ResponseExpected.getName(), itemsActual.get(0).getName());
        assertEquals(item1ResponseExpected.getDescription(), itemsActual.get(0).getDescription());
        assertEquals(item1ResponseExpected.getAvailable(), itemsActual.get(0).getAvailable());
        assertEquals(item1ResponseExpected.getNextBooking(), itemsActual.get(0).getNextBooking());
        assertEquals(item1ResponseExpected.getLastBooking(), itemsActual.get(0).getLastBooking());
        assertEquals(item1ResponseExpected.getComments(), itemsActual.get(0).getComments());
        assertEquals(item1ResponseExpected.getRequestId(), itemsActual.get(0).getRequestId());
        assertEquals(item2ResponseExpected.getId(), itemsActual.get(1).getId());
        assertEquals(item2ResponseExpected.getName(), itemsActual.get(1).getName());
        assertEquals(item2ResponseExpected.getDescription(), itemsActual.get(1).getDescription());
        assertEquals(item2ResponseExpected.getAvailable(), itemsActual.get(1).getAvailable());
        assertEquals(item2ResponseExpected.getNextBooking(), itemsActual.get(1).getNextBooking());
        assertEquals(item2ResponseExpected.getLastBooking(), itemsActual.get(1).getLastBooking());
        assertEquals(item2ResponseExpected.getComments(), itemsActual.get(1).getComments());
        assertEquals(item2ResponseExpected.getRequestId(), itemsActual.get(1).getRequestId());
    }

    @Test
    @DirtiesContext
    void updateItemShouldBeOk() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, null);
        userService.create(user);
        itemService.saveItem(itemDto1, 1);
        ItemDto itemUpdateDto = new ItemDto(null, "New item 1", "New desc 1",
                false, null, null, null, null);

        ItemDto itemResponse = itemService.updateItem(itemUpdateDto, 1, 1);
        ItemDto itemResponseExpected = new ItemDto(1, "New item 1", "New desc 1",
                false, null, null, null, null);

        assertEquals(itemResponseExpected.getId(), itemResponse.getId());
        assertEquals(itemResponseExpected.getName(), itemResponse.getName());
        assertEquals(itemResponseExpected.getDescription(), itemResponse.getDescription());
        assertEquals(itemResponseExpected.getAvailable(), itemResponse.getAvailable());
        assertEquals(itemResponseExpected.getNextBooking(), itemResponse.getNextBooking());
        assertEquals(itemResponseExpected.getLastBooking(), itemResponse.getLastBooking());
        assertEquals(itemResponseExpected.getComments(), itemResponse.getComments());
        assertEquals(itemResponseExpected.getRequestId(), itemResponse.getRequestId());
    }

    @Test
    @DirtiesContext
    void updateItemShouldThrowDataNotFoundExceptionWithNonexistentItem() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, null);
        userService.create(user);
        itemService.saveItem(itemDto1, 1);
        ItemDto itemUpdateDto = new ItemDto(null, "New item 1", "New desc 1",
                false, null, null, null, null);

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.updateItem(itemUpdateDto, 1, 2));

        assertEquals("Item with id = " + 2 + " not found", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void updateItemShouldThrowForbiddenUpdateException() {
        UserDto user1 = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto user2 = new UserDto(null, "User 2", "user2@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, null);
        userService.create(user1);
        userService.create(user2);
        itemService.saveItem(itemDto1, 1);
        ItemDto itemUpdateDto = new ItemDto(null, "New item 1", "New desc 1",
                false, null, null, null, null);

        ForbiddenUpdateException ex = assertThrows(ForbiddenUpdateException.class,
                () -> itemService.updateItem(itemUpdateDto, 2, 1));

        assertEquals("User with id = " + 2 + " does not have rights to change item " +
                "because he is not its owner", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void saveItemShouldBeOk() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, null);
        userService.create(user);

        ItemDto itemResponse = itemService.saveItem(itemDto1, 1);
        ItemDto itemResponseExpected = new ItemDto(1, "Item 1", "Desc 1", true,
                null, null, null, null);

        assertEquals(itemResponseExpected.getId(), itemResponse.getId());
        assertEquals(itemResponseExpected.getName(), itemResponse.getName());
        assertEquals(itemResponseExpected.getDescription(), itemResponse.getDescription());
        assertEquals(itemResponseExpected.getAvailable(), itemResponse.getAvailable());
        assertEquals(itemResponseExpected.getNextBooking(), itemResponse.getNextBooking());
        assertEquals(itemResponseExpected.getLastBooking(), itemResponse.getLastBooking());
        assertEquals(itemResponseExpected.getComments(), itemResponse.getComments());
        assertEquals(itemResponseExpected.getRequestId(), itemResponse.getRequestId());
    }

    @Test
    @DirtiesContext
    void saveItemShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, null);
        userService.create(user);

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.saveItem(itemDto1, 2));

        assertEquals("User with id = " + 2 + " not found", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void searchItemShouldBeOk() {
        String search = "отвертка";
        Integer userid = 1;
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Что-то", "Отвертка", true,
                null, null, null, null);
        ItemDto itemDto2 = new ItemDto(null, "Железная отвертка", "что-то", true,
                null, null, null, null);
        ItemDto itemDto3 = new ItemDto(null, "Деревяная отвертка", "Деревяная отвертка",
                true, null, null, null, null);
        ItemDto itemDto4 = new ItemDto(null, "Что-то", "Что-то",
                true, null, null, null, null);
        userService.create(user);
        itemService.saveItem(itemDto1, userid);
        itemService.saveItem(itemDto2, userid);
        itemService.saveItem(itemDto3, userid);
        itemService.saveItem(itemDto4, userid);

        assertEquals(4, repository.findAll().size());

        List<ItemDto> itemsActual = itemService.searchItem(search, userid);

        assertEquals(3, itemsActual.size());
    }

    @Test
    @DirtiesContext
    void searchItemShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        String search = "отвертка";
        Integer userid = 1;

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> itemService.searchItem(search, userid));

        assertEquals("User with id = " + userid + " not found", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void saveCommentShouldThrowBookingNotFoundException() {
        Integer userId = 1;
        Integer itemId = 1;
        UserDto user = new UserDto(null, "User 1", "user1@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, null);
        CommentResearchDto commentResearchDto = new CommentResearchDto("Comment 1", LocalDateTime.now());
        userService.create(user);
        itemService.saveItem(itemDto1, userId);

        BookingNotFoundException ex = assertThrows(BookingNotFoundException.class,
                () -> itemService.saveComment(itemId, userId, commentResearchDto));

        assertEquals("Completed bookings for a user with id = " +
                userId + " for item with id = " + itemId + " not found", ex.getMessage());
    }

    @Test
    @DirtiesContext
    void saveCommentShouldBeOk() {
        Integer userId = 1;
        Integer itemId = 1;
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(2), itemId);
        UserDto user1 = new UserDto(null, "User 1", "user1@yandex.ru");
        UserDto user2 = new UserDto(null, "User 2", "user2@yandex.ru");
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, null, null);
        CommentResearchDto commentResearchDto = new CommentResearchDto("Comment 1", LocalDateTime.now());
        userService.create(user1);
        userService.create(user2);
        itemService.saveItem(itemDto1, userId);
        bookingService.saveBooking(bookingResearchDto, 2);
        bookingService.approvedOrRejectBooking(userId, 1, true);

        CommentResponseDto commentResponse = itemService.saveComment(itemId, 2, commentResearchDto);
        CommentResponseDto commentResponseExpected = new CommentResponseDto(1, "Comment 1",
                "User 2", commentResponse.getCreated());

        assertEquals(commentResponseExpected.getId(), commentResponse.getId());
        assertEquals(commentResponseExpected.getText(), commentResponse.getText());
        assertEquals(commentResponseExpected.getAuthorName(), commentResponse.getAuthorName());
        assertEquals(commentResponseExpected.getCreated(), commentResponse.getCreated());
    }
}
