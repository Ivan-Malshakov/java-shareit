package ru.practicum.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.booking.dto.BookerDto;
import ru.practicum.booking.dto.BookingResearchDto;
import ru.practicum.booking.dto.BookingResponseDto;
import ru.practicum.booking.dto.ItemBookingResponseDto;
import ru.practicum.booking.service.BookingService;
import ru.practicum.exception.exceptions.ForbiddenAccessException;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.service.ItemService;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookingServiceIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    void saveBookingShouldThrowsForbiddenAccessException() {
        Integer itemId = 1;
        UserDto user1 = userService.create(new UserDto(null, "User 1", "user1@yandex.ru"));
        UserDto user2 = userService.create(new UserDto(null, "User 2", "user2@yandex.ru"));
        ItemDto itemDto = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto, user2.getId());
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null, LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);

        ForbiddenAccessException ex = assertThrows(
                ForbiddenAccessException.class,
                () -> bookingService.saveBooking(bookingResearchDto, user2.getId()));

        assertEquals("User with id = " + user2.getId() + " is the owner of item " +
                "with id = " + itemId, ex.getMessage());
    }

    @Test
    @DirtiesContext
    void saveBookingShouldBeOk() {
        Integer itemId = 1;
        UserDto user1 = userService.create(new UserDto(null, "User 1", "user1@yandex.ru"));
        UserDto user2 = userService.create(new UserDto(null, "User 2", "user2@yandex.ru"));
        ItemDto itemDto = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto, user1.getId());
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null, LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);

        BookingResponseDto bookingActual = bookingService.saveBooking(bookingResearchDto, user2.getId());
        BookingResponseDto bookingExpected = new BookingResponseDto(1,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getStart()),
                new ItemBookingResponseDto(1, "Item 1"), new BookerDto(user2.getId()), BookingStatus.WAITING);

        assertEquals(bookingExpected.getId(), bookingActual.getId());
        assertEquals(bookingExpected.getEnd(), bookingActual.getEnd());
        assertEquals(bookingExpected.getStart(), bookingActual.getStart());
        assertEquals(bookingExpected.getItem(), bookingActual.getItem());
        assertEquals(bookingExpected.getItem().getId(), bookingActual.getItem().getId());
        assertEquals(bookingExpected.getItem().getName(), bookingActual.getItem().getName());
        assertEquals(bookingExpected.getBooker(), bookingActual.getBooker());
        assertEquals(bookingExpected.getBooker().getId(), bookingActual.getBooker().getId());
        assertEquals(bookingExpected.getStatus(), bookingActual.getStatus());
    }

    @Test
    @DirtiesContext
    void approvedOrRejectBookingShouldThrowsForbiddenAccessException() {
        Integer itemId = 1;
        Integer bookingId = 1;
        boolean approved = true;
        UserDto user1 = userService.create(new UserDto(null, "User 1", "user1@yandex.ru"));
        UserDto user2 = userService.create(new UserDto(null, "User 2", "user2@yandex.ru"));
        ItemDto itemDto = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto, user1.getId());
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null, LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);
        bookingService.saveBooking(bookingResearchDto, user2.getId());

        ForbiddenAccessException ex = assertThrows(
                ForbiddenAccessException.class,
                () -> bookingService.approvedOrRejectBooking(user2.getId(), bookingId, approved));

        assertEquals("User with id = " + user2.getId() + " is not the owner of item" +
                " with id = " + itemId, ex.getMessage());
    }

    @Test
    @DirtiesContext
    void approvedOrRejectBookingShouldBeREJECTED() {
        Integer itemId = 1;
        Integer bookingId = 1;
        boolean approved = false;
        UserDto user1 = userService.create(new UserDto(null, "User 1", "user1@yandex.ru"));
        UserDto user2 = userService.create(new UserDto(null, "User 2", "user2@yandex.ru"));
        ItemDto itemDto = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto, user1.getId());
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(10),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(1), itemId);
        bookingService.saveBooking(bookingResearchDto, user2.getId());

        BookingResponseDto bookingActual = bookingService.approvedOrRejectBooking(user1.getId(), bookingId, approved);
        BookingResponseDto bookingExpected = new BookingResponseDto(1,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getStart()),
                new ItemBookingResponseDto(1, "Item 1"), new BookerDto(user2.getId()),
                BookingStatus.REJECTED);

        assertEquals(bookingExpected.getId(), bookingActual.getId());
        assertEquals(bookingExpected.getEnd(), bookingActual.getEnd());
        assertEquals(bookingExpected.getStart(), bookingActual.getStart());
        assertEquals(bookingExpected.getItem(), bookingActual.getItem());
        assertEquals(bookingExpected.getItem().getId(), bookingActual.getItem().getId());
        assertEquals(bookingExpected.getItem().getName(), bookingActual.getItem().getName());
        assertEquals(bookingExpected.getBooker(), bookingActual.getBooker());
        assertEquals(bookingExpected.getBooker().getId(), bookingActual.getBooker().getId());
        assertEquals(bookingExpected.getStatus(), bookingActual.getStatus());
    }

    @Test
    @DirtiesContext
    void getBookingShouldThrowsForbiddenAccessException() {
        Integer itemId = 1;
        Integer bookingId = 1;
        UserDto user1 = userService.create(new UserDto(null, "User 1", "user1@yandex.ru"));
        UserDto user2 = userService.create(new UserDto(null, "User 2", "user2@yandex.ru"));
        UserDto user3 = userService.create(new UserDto(null, "User 3", "user3@yandex.ru"));
        ItemDto itemDto = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto, user1.getId());
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null, LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);
        bookingService.saveBooking(bookingResearchDto, user2.getId());

        ForbiddenAccessException ex = assertThrows(
                ForbiddenAccessException.class,
                () -> bookingService.getBooking(user3.getId(), bookingId));

        assertEquals("User with id = " + user3.getId() + " is not the owner of item" +
                " with id = " + itemId + " or not the owner of booking with id = "
                + bookingId, ex.getMessage());
    }

    @Test
    @DirtiesContext
    void getBookingShouldBeOk() {
        Integer itemId = 1;
        Integer bookingId = 1;
        UserDto user1 = userService.create(new UserDto(null, "User 1", "user1@yandex.ru"));
        UserDto user2 = userService.create(new UserDto(null, "User 2", "user2@yandex.ru"));
        ItemDto itemDto = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto, user1.getId());
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(10),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(1), itemId);
        bookingService.saveBooking(bookingResearchDto, user2.getId());
        bookingService.approvedOrRejectBooking(user1.getId(), bookingId, true);

        BookingResponseDto bookingActual = bookingService.getBooking(user2.getId(), bookingId);
        BookingResponseDto bookingExpected = new BookingResponseDto(1,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getStart()),
                new ItemBookingResponseDto(1, "Item 1"), new BookerDto(user2.getId()),
                BookingStatus.APPROVED);

        assertEquals(bookingExpected.getId(), bookingActual.getId());
        assertEquals(bookingExpected.getEnd(), bookingActual.getEnd());
        assertEquals(bookingExpected.getStart(), bookingActual.getStart());
        assertEquals(bookingExpected.getItem(), bookingActual.getItem());
        assertEquals(bookingExpected.getItem().getId(), bookingActual.getItem().getId());
        assertEquals(bookingExpected.getItem().getName(), bookingActual.getItem().getName());
        assertEquals(bookingExpected.getBooker(), bookingActual.getBooker());
        assertEquals(bookingExpected.getBooker().getId(), bookingActual.getBooker().getId());
        assertEquals(bookingExpected.getStatus(), bookingActual.getStatus());
    }

    @Test
    @DirtiesContext
    void getBookingToUserShouldBeOk() {
        UserDto user1 = userService.create(new UserDto(null, "User 1", "user1@yandex.ru"));
        UserDto user2 = userService.create(new UserDto(null, "User 2", "user2@yandex.ru"));
        UserDto user3 = userService.create(new UserDto(null, "User 3", "user3@yandex.ru"));
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto2 = new ItemDto(null, "Item 2", "Desc 2", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto1, user2.getId());
        itemService.saveItem(itemDto2, user3.getId());
        BookingResearchDto bookingResearchDto1 = new BookingResearchDto(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now(), 1);
        BookingResearchDto bookingResearchDto2 = new BookingResearchDto(null, LocalDateTime.now().plusDays(2),
                LocalDateTime.now(), 2);
        BookingResearchDto bookingResearchDto3 = new BookingResearchDto(null, LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(8), 2);
        bookingService.saveBooking(bookingResearchDto1, user1.getId());
        bookingService.saveBooking(bookingResearchDto2, user1.getId());
        bookingService.saveBooking(bookingResearchDto3, user2.getId());
        bookingService.approvedOrRejectBooking(user2.getId(), 1, true);
        bookingService.approvedOrRejectBooking(user3.getId(), 2, true);
        bookingService.approvedOrRejectBooking(user3.getId(), 3, true);

        List<BookingResponseDto> bookingResponseDtos = bookingService.getBookingToUser(1,
                "CURRENT", 0, Integer.MAX_VALUE);

        assertEquals(2, bookingResponseDtos.size());
    }

    @Test
    @DirtiesContext
    void getBookingToOwnerShouldBeOk() {
        UserDto user1 = userService.create(new UserDto(null, "User 1", "user1@yandex.ru"));
        UserDto user2 = userService.create(new UserDto(null, "User 2", "user2@yandex.ru"));
        UserDto user3 = userService.create(new UserDto(null, "User 3", "user3@yandex.ru"));
        ItemDto itemDto1 = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto2 = new ItemDto(null, "Item 2", "Desc 2", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto3 = new ItemDto(null, "Item 3", "Desc 3", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto4 = new ItemDto(null, "Item 4", "Desc 4", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto5 = new ItemDto(null, "Item 5", "Desc 5", true,
                null, null, new ArrayList<>(), null);
        itemService.saveItem(itemDto1, user2.getId());
        itemService.saveItem(itemDto2, user3.getId());
        itemService.saveItem(itemDto3, user3.getId());
        itemService.saveItem(itemDto4, user3.getId());
        itemService.saveItem(itemDto5, user1.getId());
        BookingResearchDto bookingResearchDto1 = new BookingResearchDto(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now(), 1);
        BookingResearchDto bookingResearchDto2 = new BookingResearchDto(null, LocalDateTime.now().plusDays(2),
                LocalDateTime.now(), 2);
        BookingResearchDto bookingResearchDto3 = new BookingResearchDto(null, LocalDateTime.now().plusDays(10),
                LocalDateTime.now(), 3);
        BookingResearchDto bookingResearchDto4 = new BookingResearchDto(null, LocalDateTime.now().plusDays(100),
                LocalDateTime.now().plusDays(10), 4);
        BookingResearchDto bookingResearchDto5 = new BookingResearchDto(null, LocalDateTime.now().plusDays(5),
                LocalDateTime.now(), 5);
        bookingService.saveBooking(bookingResearchDto1, user1.getId());
        bookingService.saveBooking(bookingResearchDto2, user2.getId());
        bookingService.saveBooking(bookingResearchDto3, user1.getId());
        bookingService.saveBooking(bookingResearchDto4, user1.getId());
        bookingService.saveBooking(bookingResearchDto5, user3.getId());
        bookingService.approvedOrRejectBooking(user2.getId(), 1, true);
        bookingService.approvedOrRejectBooking(user3.getId(), 2, true);
        bookingService.approvedOrRejectBooking(user3.getId(), 3, true);
        bookingService.approvedOrRejectBooking(user3.getId(), 4, true);
        bookingService.approvedOrRejectBooking(user1.getId(), 5, true);

        List<BookingResponseDto> bookingResponseDtos = bookingService.getBookingToOwner(3,
                "CURRENT", 0, Integer.MAX_VALUE);

        assertEquals(2, bookingResponseDtos.size());
    }
}
