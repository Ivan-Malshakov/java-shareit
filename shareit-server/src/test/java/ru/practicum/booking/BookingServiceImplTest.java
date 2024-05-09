package ru.practicum.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.booking.dto.BookerDto;
import ru.practicum.booking.dto.BookingResearchDto;
import ru.practicum.booking.dto.BookingResponseDto;
import ru.practicum.booking.dto.ItemBookingResponseDto;
import ru.practicum.booking.service.BookingService;
import ru.practicum.booking.service.BookingServiceImpl;
import ru.practicum.booking.storage.JpaBookingRepository;
import ru.practicum.exception.exceptions.*;
import ru.practicum.item.Item;
import ru.practicum.item.service.ItemService;
import ru.practicum.user.User;
import ru.practicum.user.UserMapperImpl;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    private BookingService bookingService;
    @Mock
    JpaBookingRepository bookingRepository;
    @Mock
    UserService userService;
    @Mock
    ItemService itemService;

    @BeforeEach
    void set() {
        bookingService = new BookingServiceImpl(bookingRepository, userService,
                itemService, new BookingMapperImpl(), new UserMapperImpl());
    }

    @Test
    void saveBookingShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer userId = 1;
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null, LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), userId);

        when(userService.getData(anyInt())).thenThrow(new DataNotFoundException(
                "User with id = " + userId + " not found"));

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> bookingService.saveBooking(bookingResearchDto, userId));

        assertEquals("User with id = " + userId + " not found", ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void saveBookingShouldThrowDataNotFoundExceptionWithNonexistentItem() {
        Integer userId = 1;
        Integer itemId = 1;
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null, LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), userId);
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(itemService.getItemToBooking(itemId)).thenThrow(new DataNotFoundException(
                "Item with id = " + itemId + " not found"));

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> bookingService.saveBooking(bookingResearchDto, userId));

        assertEquals("Item with id = " + itemId + " not found", ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void saveBookingShouldThrowBookingDateExceptionWithDateEndBookingBeforeStart() {
        Integer userId = 1;
        Integer itemId = 1;
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null, LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), itemId);
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        Item item1 = new Item(1, "Item 1", "Desc 1", true, user2, null);

        when(userService.getData(anyInt())).thenReturn(user1);
        when(itemService.getItemToBooking(itemId)).thenReturn(item1);

        BookingDateException ex = assertThrows(BookingDateException.class,
                () -> bookingService.saveBooking(bookingResearchDto, userId));

        assertEquals("The end date of booking is earlier or equal than to the start date", ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void saveBookingShouldThrowBookingDateExceptionWithDateEndBookingStartEqualsEnd() {
        Integer userId = 1;
        Integer itemId = 1;
        LocalDateTime start = LocalDateTime.now();
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null, start,
                start, itemId);
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        Item item1 = new Item(1, "Item 1", "Desc 1", true, user2, null);

        when(userService.getData(anyInt())).thenReturn(user1);
        when(itemService.getItemToBooking(itemId)).thenReturn(item1);

        BookingDateException ex = assertThrows(BookingDateException.class,
                () -> bookingService.saveBooking(bookingResearchDto, userId));

        assertEquals("The end date of booking is earlier or equal than to the start date", ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void saveBookingShouldThrowUnavailableItemException() {
        Integer userId = 1;
        Integer itemId = 1;
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null, LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        Item item1 = new Item(1, "Item 1", "Desc 1", false, user2, null);

        when(userService.getData(anyInt())).thenReturn(user1);
        when(itemService.getItemToBooking(itemId)).thenReturn(item1);

        UnavailableItemException ex = assertThrows(UnavailableItemException.class,
                () -> bookingService.saveBooking(bookingResearchDto, userId));

        assertEquals("Item with id = " + item1.getId() + " unavailable", ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void saveBookingShouldThrowForbiddenAccessException() {
        Integer userId = 1;
        Integer itemId = 1;
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null, LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        Item item1 = new Item(1, "Item 1", "Desc 1", true, user1, null);

        when(userService.getData(anyInt())).thenReturn(user1Dto);
        when(itemService.getItemToBooking(itemId)).thenReturn(item1);

        ForbiddenAccessException ex = assertThrows(ForbiddenAccessException.class,
                () -> bookingService.saveBooking(bookingResearchDto, userId));

        assertEquals("User with id = " + user1.getId() + " is the owner of item " +
                "with id = " + item1.getId(), ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void saveBookingShouldBeOk() {
        Integer userId = 1;
        Integer itemId = 1;
        BookingResearchDto bookingResearchDto = new BookingResearchDto(null, LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(1), itemId);
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        Item item1 = new Item(1, "Item 1", "Desc 1", true, user2, null);
        Booking booking = new Booking(1, bookingResearchDto.getStart(), bookingResearchDto.getEnd(), item1,
                user1, BookingStatus.WAITING);

        when(userService.getData(anyInt())).thenReturn(user1Dto);
        when(itemService.getItemToBooking(itemId)).thenReturn(item1);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        ItemBookingResponseDto item = new ItemBookingResponseDto(1, "Item 1");
        BookerDto bookerDto = new BookerDto(userId);
        BookingResponseDto bookingExpected = new BookingResponseDto(1,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(bookingResearchDto.getStart()), item, bookerDto,
                BookingStatus.WAITING);
        BookingResponseDto bookingActual = bookingService.saveBooking(bookingResearchDto, userId);

        assertEquals(bookingExpected.getId(), bookingActual.getId());
        assertEquals(bookingExpected.getEnd(), bookingActual.getEnd());
        assertEquals(bookingExpected.getStart(), bookingActual.getStart());
        assertEquals(bookingExpected.getItem(), bookingActual.getItem());
        assertEquals(bookingExpected.getItem().getId(), bookingActual.getItem().getId());
        assertEquals(bookingExpected.getItem().getName(), bookingActual.getItem().getName());
        assertEquals(bookingExpected.getBooker(), bookingActual.getBooker());
        assertEquals(bookingExpected.getBooker().getId(), bookingActual.getBooker().getId());
        assertEquals(bookingExpected.getStatus(), bookingActual.getStatus());
        verify(bookingRepository, atLeast(1)).save(any(Booking.class));
    }

    @Test
    void approvedOrRejectBookingShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer userId = 1;
        Integer bookingId = 1;
        boolean status = true;

        when(userService.getData(anyInt())).thenThrow(new DataNotFoundException(
                "User with id = " + userId + " not found"));

        DataNotFoundException ex = assertThrows(
                DataNotFoundException.class,
                () -> bookingService.approvedOrRejectBooking(userId, bookingId, status));

        assertEquals("User with id = " + userId + " not found", ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void approvedOrRejectBookingShouldThrowDataNotFoundExceptionWithNonexistentBooking() {
        Integer userId = 1;
        Integer bookingId = 1;
        boolean status = true;
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1Dto);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        DataNotFoundException ex = assertThrows(
                DataNotFoundException.class,
                () -> bookingService.approvedOrRejectBooking(userId, bookingId, status));

        assertEquals("Booking with id = " + bookingId + " not found", ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void approvedOrRejectBookingShouldThrowForbiddenAccessException() {
        Integer userId = 1;
        Integer bookingId = 1;
        boolean status = true;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        Item item1 = new Item(1, "Item 1", "Desc 1", true, user2, null);
        Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10), item1,
                user1, BookingStatus.WAITING);

        when(userService.getData(anyInt())).thenReturn(user1Dto);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        ForbiddenAccessException ex = assertThrows(ForbiddenAccessException.class,
                () -> bookingService.approvedOrRejectBooking(userId, bookingId, status));

        assertEquals("User with id = " + userId + " is not the owner of item" +
                " with id = " + booking.getItem().getId(), ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void approvedOrRejectBookingShouldThrowForbiddenAccessChangeStatusException() {
        Integer userId = 2;
        Integer bookingId = 1;
        boolean status = true;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        UserDto user2Dto = new UserDto(2, "User 2", "user2@yandex.ru");
        Item item1 = new Item(1, "Item 1", "Desc 1", true, user2, null);
        Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10), item1,
                user1, BookingStatus.REJECTED);

        when(userService.getData(anyInt())).thenReturn(user2Dto);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        ForbiddenAccessChangeStatusException ex = assertThrows(ForbiddenAccessChangeStatusException.class,
                () -> bookingService.approvedOrRejectBooking(userId, bookingId, status));

        assertEquals("Not access to status change", ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void approvedOrRejectBookingShouldBeApproved() {
        Integer userId = 2;
        Integer bookingId = 1;
        boolean status = true;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        UserDto user2Dto = new UserDto(2, "User 2", "user2@yandex.ru");
        Item item1 = new Item(1, "Item 1", "Desc 1", true, user2, null);
        ItemBookingResponseDto itemBookingResponseDto = new ItemBookingResponseDto(1, "Item 1");
        BookerDto bookerDto = new BookerDto(user1.getId());
        Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10), item1,
                user1, BookingStatus.WAITING);
        Booking bookingApproved = new Booking(1, booking.getStart(), booking.getEnd(), item1, user1,
                BookingStatus.APPROVED);

        when(userService.getData(anyInt())).thenReturn(user2Dto);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingApproved);
        BookingResponseDto bookingExpected = new BookingResponseDto(1,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getStart()), itemBookingResponseDto, bookerDto,
                BookingStatus.APPROVED);
        BookingResponseDto bookingActual = bookingService.approvedOrRejectBooking(userId, bookingId, status);

        assertEquals(bookingExpected.getId(), bookingActual.getId());
        assertEquals(bookingExpected.getEnd(), bookingActual.getEnd());
        assertEquals(bookingExpected.getStart(), bookingActual.getStart());
        assertEquals(bookingExpected.getItem(), bookingActual.getItem());
        assertEquals(bookingExpected.getItem().getId(), bookingActual.getItem().getId());
        assertEquals(bookingExpected.getItem().getName(), bookingActual.getItem().getName());
        assertEquals(bookingExpected.getBooker(), bookingActual.getBooker());
        assertEquals(bookingExpected.getBooker().getId(), bookingActual.getBooker().getId());
        assertEquals(bookingExpected.getStatus(), bookingActual.getStatus());
        verify(bookingRepository, atLeast(1)).save(any(Booking.class));
    }

    @Test
    void approvedOrRejectBookingShouldBeRejected() {
        Integer userId = 2;
        Integer bookingId = 1;
        boolean status = false;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        UserDto user2Dto = new UserDto(2, "User 2", "user2@yandex.ru");
        Item item1 = new Item(1, "Item 1", "Desc 1", true, user2, null);
        ItemBookingResponseDto itemBookingResponseDto = new ItemBookingResponseDto(1, "Item 1");
        BookerDto bookerDto = new BookerDto(user1.getId());
        Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10), item1,
                user1, BookingStatus.WAITING);
        Booking bookingRejected = new Booking(1, booking.getStart(), booking.getEnd(), item1, user1,
                BookingStatus.REJECTED);

        when(userService.getData(anyInt())).thenReturn(user2Dto);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingRejected);
        BookingResponseDto bookingExpected = new BookingResponseDto(1,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getStart()), itemBookingResponseDto, bookerDto,
                BookingStatus.REJECTED);
        BookingResponseDto bookingActual = bookingService.approvedOrRejectBooking(userId, bookingId, status);

        assertEquals(bookingExpected.getId(), bookingActual.getId());
        assertEquals(bookingExpected.getEnd(), bookingActual.getEnd());
        assertEquals(bookingExpected.getStart(), bookingActual.getStart());
        assertEquals(bookingExpected.getItem(), bookingActual.getItem());
        assertEquals(bookingExpected.getItem().getId(), bookingActual.getItem().getId());
        assertEquals(bookingExpected.getItem().getName(), bookingActual.getItem().getName());
        assertEquals(bookingExpected.getBooker(), bookingActual.getBooker());
        assertEquals(bookingExpected.getBooker().getId(), bookingActual.getBooker().getId());
        assertEquals(bookingExpected.getStatus(), bookingActual.getStatus());
        verify(bookingRepository, atLeast(1)).save(any(Booking.class));
    }

    @Test
    void getBookingShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer userId = 1;
        Integer bookingId = 1;
        User user1 = new User(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenThrow(new DataNotFoundException(
                "User with id = " + userId + " not found"));

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> bookingService.getBooking(userId, bookingId));

        assertEquals("User with id = " + userId + " not found", ex.getMessage());
    }

    @Test
    void getBookingShouldThrowDataNotFoundExceptionWithNonexistentBooking() {
        Integer userId = 1;
        Integer bookingId = 1;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> bookingService.getBooking(userId, bookingId));

        assertEquals("Booking with id = " + bookingId + " not found", ex.getMessage());
    }

    @Test
    void getBookingShouldThrowForbiddenAccessException() {
        Integer userId = 3;
        Integer bookingId = 1;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        UserDto user3Dto = new UserDto(3, "User 3", "user3@yandex.ru");
        Item item1 = new Item(1, "Item 1", "Desc 1", true, user2, null);
        Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10), item1,
                user1, BookingStatus.APPROVED);

        when(userService.getData(anyInt())).thenReturn(user3Dto);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        ForbiddenAccessException ex = assertThrows(ForbiddenAccessException.class,
                () -> bookingService.getBooking(userId, bookingId));

        assertEquals("User with id = " + userId + " is not the owner of item" +
                " with id = " + item1.getId() + " or not the owner of booking with id = " + bookingId, ex.getMessage());
    }

    @Test
    void getBookingShouldBeOk() {
        Integer userId = 1;
        Integer bookingId = 1;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        UserDto user1Dto = new UserDto(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        Item item1 = new Item(1, "Item 1", "Desc 1", true, user2, null);
        Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10), item1,
                user1, BookingStatus.APPROVED);
        ItemBookingResponseDto itemBookingResponseDto = new ItemBookingResponseDto(1, "Item 1");
        BookerDto bookerDto = new BookerDto(user1.getId());

        when(userService.getData(anyInt())).thenReturn(user1Dto);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        BookingResponseDto bookingExpected = new BookingResponseDto(1,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getStart()), itemBookingResponseDto, bookerDto,
                BookingStatus.APPROVED);
        BookingResponseDto bookingActual = bookingService.getBooking(userId, bookingId);

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
    void getBookingShouldBeOkWithOwner() {
        Integer userId = 2;
        Integer bookingId = 1;
        User user1 = new User(1, "User 1", "user1@yandex.ru");
        User user2 = new User(2, "User 2", "user2@yandex.ru");
        UserDto user2Dto = new UserDto(2, "User 2", "user2@yandex.ru");
        Item item1 = new Item(1, "Item 1", "Desc 1", true, user2, null);
        Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10), item1,
                user1, BookingStatus.APPROVED);
        ItemBookingResponseDto itemBookingResponseDto = new ItemBookingResponseDto(1, "Item 1");
        BookerDto bookerDto = new BookerDto(user1.getId());

        when(userService.getData(anyInt())).thenReturn(user2Dto);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        BookingResponseDto bookingExpected = new BookingResponseDto(1,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getEnd()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(booking.getStart()), itemBookingResponseDto, bookerDto,
                BookingStatus.APPROVED);
        BookingResponseDto bookingActual = bookingService.getBooking(userId, bookingId);

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
    void getBookingToUserShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer userId = 1;
        String state = "";
        Integer from = 0;
        Integer size = 20;
        User user1 = new User(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenThrow(new DataNotFoundException(
                "User with id = " + userId + " not found"));

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> bookingService.getBookingToUser(userId, state, from, size));

        assertEquals("User with id = " + userId + " not found", ex.getMessage());
    }

    @Test
    void getBookingToUserShouldThrowStateNotFoundExceptionWithUnknownState() {
        Integer userId = 1;
        String state = "STATE";
        Integer from = 0;
        Integer size = 20;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);

        StateNotFoundException ex = assertThrows(StateNotFoundException.class,
                () -> bookingService.getBookingToUser(userId, state, from, size));

        assertEquals("Unknown state: " + state, ex.getMessage());
    }

    @Test
    void getBookingToUserShouldBeOkWithStateALL() {
        Integer userId = 1;
        String state = "ALL";
        Integer from = 0;
        Integer size = 20;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(bookingRepository.findBookingByBooker_IdOrderByStartDesc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToUser(userId, state, from, size);

        verify(bookingRepository, atLeast(1)).findBookingByBooker_IdOrderByStartDesc(anyInt());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToUserShouldBeOkWithStateCURRENT() {
        Integer userId = 1;
        String state = "CURRENT";
        Integer from = 0;
        Integer size = 20;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(bookingRepository.findBookingByBooker_IdOrderByStartDesc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToUser(userId, state, from, size);

        verify(bookingRepository, atLeast(1)).findBookingByBooker_IdOrderByStartDesc(anyInt());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToUserShouldBeOkWithStatePAST() {
        Integer userId = 1;
        String state = "PAST";
        Integer from = 0;
        Integer size = 20;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(bookingRepository.findBookingByBooker_IdOrderByStartDesc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToUser(userId, state, from, size);

        verify(bookingRepository, atLeast(1)).findBookingByBooker_IdOrderByStartDesc(anyInt());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToUserShouldBeOkWithStateFUTURE() {
        Integer userId = 1;
        String state = "FUTURE";
        Integer from = 0;
        Integer size = 20;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(bookingRepository.findBookingByBooker_IdOrderByStartDesc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToUser(userId, state, from, size);

        verify(bookingRepository, atLeast(1)).findBookingByBooker_IdOrderByStartDesc(anyInt());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToUserShouldBeOkWithStateWAITING() {
        Integer userId = 1;
        String state = "WAITING";
        Integer from = 0;
        Integer size = 20;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(bookingRepository.findBookingByBooker_IdAndStatus(anyInt(), any(BookingStatus.class)))
                .thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToUser(userId, state, from, size);

        verify(bookingRepository, atLeast(1)).findBookingByBooker_IdAndStatus(anyInt(),
                any(BookingStatus.class));
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToUserShouldBeOkWithStateREJECTED() {
        Integer userId = 1;
        String state = "REJECTED";
        Integer from = 0;
        Integer size = 20;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(bookingRepository.findBookingByBooker_IdAndStatus(anyInt(), any(BookingStatus.class)))
                .thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToUser(userId, state, from, size);

        verify(bookingRepository, atLeast(1)).findBookingByBooker_IdAndStatus(anyInt(),
                any(BookingStatus.class));
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToOwnerShouldThrowDataNotFoundExceptionWithNonexistentUser() {
        Integer userId = 1;
        String state = "";
        Integer from = 0;
        Integer size = 20;
        User user1 = new User(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenThrow(new DataNotFoundException(
                "User with id = " + userId + " not found"));

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> bookingService.getBookingToOwner(userId, state, from, size));

        assertEquals("User with id = " + userId + " not found", ex.getMessage());
    }

    @Test
    void getBookingToOwnerShouldThrowStateNotFoundExceptionWithUnknownState() {
        Integer userId = 1;
        String state = "STATE";
        Integer from = 0;
        Integer size = 20;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);

        StateNotFoundException ex = assertThrows(StateNotFoundException.class,
                () -> bookingService.getBookingToOwner(userId, state, from, size));

        assertEquals("Unknown state: " + state, ex.getMessage());
    }

    @Test
    void getBookingToOwnerShouldBeOkWithStateALL() {
        Integer userId = 1;
        String state = "ALL";
        Integer from = 0;
        Integer size = 20;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(bookingRepository.findBookingByItem_Owner_IdOrderByStartDesc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToOwner(userId, state, from, size);

        verify(bookingRepository, atLeast(1)).findBookingByItem_Owner_IdOrderByStartDesc(anyInt());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToOwnerShouldBeOkWithStateCURRENT() {
        Integer userId = 1;
        String state = "CURRENT";
        Integer from = 0;
        Integer size = 20;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(bookingRepository.findBookingByItem_Owner_IdOrderByStartDesc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToOwner(userId, state, from, size);

        verify(bookingRepository, atLeast(1)).findBookingByItem_Owner_IdOrderByStartDesc(anyInt());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToOwnerShouldBeOkWithStatePAST() {
        Integer userId = 1;
        String state = "PAST";
        Integer from = 0;
        Integer size = 20;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(bookingRepository.findBookingByItem_Owner_IdOrderByStartDesc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToOwner(userId, state, from, size);

        verify(bookingRepository, atLeast(1)).findBookingByItem_Owner_IdOrderByStartDesc(anyInt());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToOwnerShouldBeOkWithStateFUTURE() {
        Integer userId = 1;
        String state = "FUTURE";
        Integer from = 0;
        Integer size = 20;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(bookingRepository.findBookingByItem_Owner_IdOrderByStartDesc(anyInt())).thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToOwner(userId, state, from, size);

        verify(bookingRepository, atLeast(1)).findBookingByItem_Owner_IdOrderByStartDesc(anyInt());
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToOwnerShouldBeOkWithStateWAITING() {
        Integer userId = 1;
        String state = "WAITING";
        Integer from = 0;
        Integer size = 20;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(bookingRepository.findBookingByItem_Owner_IdAndStatus(anyInt(), any(BookingStatus.class)))
                .thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToOwner(userId, state, from, size);

        verify(bookingRepository, atLeast(1)).findBookingByItem_Owner_IdAndStatus(anyInt(),
                any(BookingStatus.class));
        assertEquals(0, bookingsResponse.size());
    }

    @Test
    void getBookingToOwnerShouldBeOkWithStateREJECTED() {
        Integer userId = 1;
        String state = "REJECTED";
        Integer from = 0;
        Integer size = 20;
        UserDto user1 = new UserDto(1, "User 1", "user1@yandex.ru");

        when(userService.getData(anyInt())).thenReturn(user1);
        when(bookingRepository.findBookingByItem_Owner_IdAndStatus(anyInt(), any(BookingStatus.class)))
                .thenReturn(new ArrayList<>());
        List<BookingResponseDto> bookingsResponse = bookingService.getBookingToOwner(userId, state, from, size);

        verify(bookingRepository, atLeast(1)).findBookingByItem_Owner_IdAndStatus(anyInt(),
                any(BookingStatus.class));
        assertEquals(0, bookingsResponse.size());
    }
}
