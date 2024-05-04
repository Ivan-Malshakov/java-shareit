package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingResearchDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ItemBookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService service;
    @Autowired
    private MockMvc mvc;

    @Test
    void saveBookingShouldThrowsValidationExceptionWithItemIdNull() throws Exception {
        BookingResearchDto bookingResearchDto = new BookingResearchDto();
        bookingResearchDto.setStart(LocalDateTime.now().plusDays(10));
        bookingResearchDto.setEnd(LocalDateTime.now().plusDays(20));

        when(service.saveBooking(any(BookingResearchDto.class), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    BookingResearchDto bookingResearch = invocationOnMock.getArgument(0, BookingResearchDto.class);
                    BookingResponseDto bookingResponseDto = new BookingResponseDto();
                    bookingResponseDto.setBooker(new BookerDto(invocationOnMock.getArgument(1, Integer.class)));
                    bookingResponseDto.setId(1);
                    bookingResponseDto.setItem(new ItemBookingResponseDto(bookingResearch.getItemId(), "Item 1"));
                    bookingResponseDto.setStatus(BookingStatus.WAITING);
                    bookingResponseDto.setStart(bookingResearch.getStart()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    bookingResponseDto.setEnd(bookingResearch.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return bookingResponseDto;
                });

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingResearchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveBookingShouldThrowsValidationExceptionWithStartNull() throws Exception {
        BookingResearchDto bookingResearchDto = new BookingResearchDto();
        bookingResearchDto.setItemId(1);
        bookingResearchDto.setEnd(LocalDateTime.now().plusDays(20));

        when(service.saveBooking(any(BookingResearchDto.class), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    BookingResearchDto bookingResearch = invocationOnMock.getArgument(0, BookingResearchDto.class);
                    BookingResponseDto bookingResponseDto = new BookingResponseDto();
                    bookingResponseDto.setBooker(new BookerDto(invocationOnMock.getArgument(1, Integer.class)));
                    bookingResponseDto.setId(1);
                    bookingResponseDto.setItem(new ItemBookingResponseDto(bookingResearch.getItemId(), "Item 1"));
                    bookingResponseDto.setStatus(BookingStatus.WAITING);
                    bookingResponseDto.setStart(bookingResearch.getStart()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    bookingResponseDto.setEnd(bookingResearch.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return bookingResponseDto;
                });

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingResearchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveBookingShouldThrowsValidationExceptionWithEndNull() throws Exception {
        BookingResearchDto bookingResearchDto = new BookingResearchDto();
        bookingResearchDto.setItemId(1);
        bookingResearchDto.setStart(LocalDateTime.now().plusDays(20));

        when(service.saveBooking(any(BookingResearchDto.class), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    BookingResearchDto bookingResearch = invocationOnMock.getArgument(0, BookingResearchDto.class);
                    BookingResponseDto bookingResponseDto = new BookingResponseDto();
                    bookingResponseDto.setBooker(new BookerDto(invocationOnMock.getArgument(1, Integer.class)));
                    bookingResponseDto.setId(1);
                    bookingResponseDto.setItem(new ItemBookingResponseDto(bookingResearch.getItemId(), "Item 1"));
                    bookingResponseDto.setStatus(BookingStatus.WAITING);
                    bookingResponseDto.setStart(bookingResearch.getStart()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    bookingResponseDto.setEnd(bookingResearch.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return bookingResponseDto;
                });
    }

    @Test
    void saveBookingShouldThrowsValidationExceptionWithStartPast() throws Exception {
        BookingResearchDto bookingResearchDto = new BookingResearchDto();
        bookingResearchDto.setItemId(1);
        bookingResearchDto.setStart(LocalDateTime.now().minusSeconds(1));
        bookingResearchDto.setEnd(LocalDateTime.now().plusDays(1));

        when(service.saveBooking(any(BookingResearchDto.class), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    BookingResearchDto bookingResearch = invocationOnMock.getArgument(0, BookingResearchDto.class);
                    BookingResponseDto bookingResponseDto = new BookingResponseDto();
                    bookingResponseDto.setBooker(new BookerDto(invocationOnMock.getArgument(1, Integer.class)));
                    bookingResponseDto.setId(1);
                    bookingResponseDto.setItem(new ItemBookingResponseDto(bookingResearch.getItemId(), "Item 1"));
                    bookingResponseDto.setStatus(BookingStatus.WAITING);
                    bookingResponseDto.setStart(bookingResearch.getStart()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    bookingResponseDto.setEnd(bookingResearch.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return bookingResponseDto;
                });

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingResearchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveBookingShouldThrowValidationExceptionWithInvalidUserIdInHeader() throws Exception {
        BookingResearchDto bookingResearchDto = new BookingResearchDto();
        bookingResearchDto.setItemId(1);
        bookingResearchDto.setStart(LocalDateTime.now().plusDays(10));
        bookingResearchDto.setEnd(LocalDateTime.now().plusDays(20));
        Integer userId = 0;

        when(service.saveBooking(any(BookingResearchDto.class), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    BookingResearchDto bookingResearch = invocationOnMock.getArgument(0, BookingResearchDto.class);
                    BookingResponseDto bookingResponseDto = new BookingResponseDto();
                    bookingResponseDto.setBooker(new BookerDto(invocationOnMock.getArgument(1, Integer.class)));
                    bookingResponseDto.setId(1);
                    bookingResponseDto.setItem(new ItemBookingResponseDto(bookingResearch.getItemId(), "Item 1"));
                    bookingResponseDto.setStatus(BookingStatus.WAITING);
                    bookingResponseDto.setStart(bookingResearch.getStart()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    bookingResponseDto.setEnd(bookingResearch.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return bookingResponseDto;
                });

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingResearchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveBookingShouldThrowValidationExceptionWithoutUserIdInHeader() throws Exception {
        BookingResearchDto bookingResearchDto = new BookingResearchDto();
        bookingResearchDto.setItemId(1);
        bookingResearchDto.setStart(LocalDateTime.now().plusDays(10));
        bookingResearchDto.setEnd(LocalDateTime.now().plusDays(20));

        when(service.saveBooking(any(BookingResearchDto.class), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    BookingResearchDto bookingResearch = invocationOnMock.getArgument(0, BookingResearchDto.class);
                    BookingResponseDto bookingResponseDto = new BookingResponseDto();
                    bookingResponseDto.setBooker(new BookerDto(invocationOnMock.getArgument(1, Integer.class)));
                    bookingResponseDto.setId(1);
                    bookingResponseDto.setItem(new ItemBookingResponseDto(bookingResearch.getItemId(), "Item 1"));
                    bookingResponseDto.setStatus(BookingStatus.WAITING);
                    bookingResponseDto.setStart(bookingResearch.getStart()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    bookingResponseDto.setEnd(bookingResearch.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return bookingResponseDto;
                });

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "")
                        .content(mapper.writeValueAsString(bookingResearchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void saveBookingTestShouldBeOk() throws Exception {
        BookingResearchDto bookingResearchDto = new BookingResearchDto();
        bookingResearchDto.setItemId(1);
        bookingResearchDto.setStart(LocalDateTime.now().plusDays(10));
        bookingResearchDto.setEnd(LocalDateTime.now().plusDays(20));

        when(service.saveBooking(any(BookingResearchDto.class), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    BookingResearchDto bookingResearch = invocationOnMock.getArgument(0, BookingResearchDto.class);
                    BookingResponseDto bookingResponseDto = new BookingResponseDto();
                    bookingResponseDto.setBooker(new BookerDto(invocationOnMock.getArgument(1, Integer.class)));
                    bookingResponseDto.setId(1);
                    bookingResponseDto.setItem(new ItemBookingResponseDto(bookingResearch.getItemId(), "Item 1"));
                    bookingResponseDto.setStatus(BookingStatus.WAITING);
                    bookingResponseDto.setStart(bookingResearch.getStart()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    bookingResponseDto.setEnd(bookingResearch.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return bookingResponseDto;
                });

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingResearchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.end", is(bookingResearchDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.start", is(bookingResearchDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(1), Integer.class))
                .andExpect(jsonPath("$.item.name", is("Item 1")))
                .andExpect(jsonPath("$.booker.id", is(1), Integer.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    void approvedOrRejectBookingIsApprovedShouldBeOk() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setBooker(new BookerDto(1));
        bookingResponseDto.setId(1);
        bookingResponseDto.setItem(new ItemBookingResponseDto(1, "Item 1"));
        bookingResponseDto.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        when(service.approvedOrRejectBooking(anyInt(), anyInt(), any(Boolean.class)))
                .thenAnswer(invocationOnMock -> {
                    if (invocationOnMock.getArgument(2, Boolean.class)) {
                        bookingResponseDto.setStatus(BookingStatus.APPROVED);
                    } else {
                        bookingResponseDto.setStatus(BookingStatus.REJECTED);
                    }
                    return bookingResponseDto;
                });

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd())))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart())))
                .andExpect(jsonPath("$.item.id", is(1), Integer.class))
                .andExpect(jsonPath("$.item.name", is("Item 1")))
                .andExpect(jsonPath("$.booker.id", is(1), Integer.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void approvedOrRejectBookingIsRejectedShouldBeOk() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setBooker(new BookerDto(1));
        bookingResponseDto.setId(1);
        bookingResponseDto.setItem(new ItemBookingResponseDto(1, "Item 1"));
        bookingResponseDto.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        when(service.approvedOrRejectBooking(anyInt(), anyInt(), any(Boolean.class)))
                .thenAnswer(invocationOnMock -> {
                    if (invocationOnMock.getArgument(2, Boolean.class)) {
                        bookingResponseDto.setStatus(BookingStatus.APPROVED);
                    } else {
                        bookingResponseDto.setStatus(BookingStatus.REJECTED);
                    }
                    return bookingResponseDto;
                });

        mvc.perform(patch("/bookings/1?approved=false")
                        .header("X-Sharer-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd())))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart())))
                .andExpect(jsonPath("$.item.id", is(1), Integer.class))
                .andExpect(jsonPath("$.item.name", is("Item 1")))
                .andExpect(jsonPath("$.booker.id", is(1), Integer.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.REJECTED.toString())));
    }

    @Test
    void approvedOrRejectBookingShouldThrowValidationExceptionWithInvalidUserIdInHeader() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setBooker(new BookerDto(1));
        bookingResponseDto.setId(1);
        bookingResponseDto.setItem(new ItemBookingResponseDto(1, "Item 1"));
        bookingResponseDto.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        Integer userId = 0;

        when(service.approvedOrRejectBooking(anyInt(), anyInt(), any(Boolean.class)))
                .thenAnswer(invocationOnMock -> {
                    if (invocationOnMock.getArgument(2, Boolean.class)) {
                        bookingResponseDto.setStatus(BookingStatus.APPROVED);
                    } else {
                        bookingResponseDto.setStatus(BookingStatus.REJECTED);
                    }
                    return bookingResponseDto;
                });

        mvc.perform(patch("/bookings/1?approved=false")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approvedOrRejectBookingShouldThrowValidationExceptionWithoutUserIdInHeader() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setBooker(new BookerDto(1));
        bookingResponseDto.setId(1);
        bookingResponseDto.setItem(new ItemBookingResponseDto(1, "Item 1"));
        bookingResponseDto.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        when(service.approvedOrRejectBooking(anyInt(), anyInt(), any(Boolean.class)))
                .thenAnswer(invocationOnMock -> {
                    if (invocationOnMock.getArgument(2, Boolean.class)) {
                        bookingResponseDto.setStatus(BookingStatus.APPROVED);
                    } else {
                        bookingResponseDto.setStatus(BookingStatus.REJECTED);
                    }
                    return bookingResponseDto;
                });

        mvc.perform(patch("/bookings/1?approved=false")
                        .header("X-Sharer-User-Id", "")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void approvedOrRejectBookingShouldThrowValidationExceptionWithInvalidBookingId() throws Exception {
        mvc.perform(patch("/bookings/0?approved=false")
                        .header("X-Sharer-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approvedOrRejectBookingShouldThrowValidationExceptionWithoutBookingId() throws Exception {
        mvc.perform(patch("/bookings/?approved=false")
                        .header("X-Sharer-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getBookingTestShouldBeOk() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setBooker(new BookerDto(1));
        bookingResponseDto.setId(1);
        bookingResponseDto.setItem(new ItemBookingResponseDto(1, "Item 1"));
        bookingResponseDto.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto.setStatus(BookingStatus.APPROVED);

        when(service.getBooking(anyInt(), anyInt()))
                .thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd())))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart())))
                .andExpect(jsonPath("$.item.id", is(1), Integer.class))
                .andExpect(jsonPath("$.item.name", is("Item 1")))
                .andExpect(jsonPath("$.booker.id", is(1), Integer.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void getBookingShouldThrowValidationExceptionWithInvalidBookingId() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setBooker(new BookerDto(1));
        bookingResponseDto.setId(1);
        bookingResponseDto.setItem(new ItemBookingResponseDto(1, "Item 1"));
        bookingResponseDto.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto.setStatus(BookingStatus.APPROVED);

        when(service.getBooking(anyInt(), anyInt()))
                .thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/0")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingShouldThrowValidationExceptionWithInvalidUserIdInHeader() throws Exception {
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 0)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingShouldThrowValidationExceptionWithoutUserIdInHeader() throws Exception {
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getBookingsToBookerWithNullSizeShouldBeOk() throws Exception {
        BookingResponseDto bookingResponseDto1 = new BookingResponseDto();
        bookingResponseDto1.setBooker(new BookerDto(1));
        bookingResponseDto1.setId(1);
        bookingResponseDto1.setItem(new ItemBookingResponseDto(1, "Item 1"));
        bookingResponseDto1.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setStatus(BookingStatus.APPROVED);
        BookingResponseDto bookingResponseDto2 = new BookingResponseDto();
        bookingResponseDto2.setBooker(new BookerDto(1));
        bookingResponseDto2.setId(2);
        bookingResponseDto2.setItem(new ItemBookingResponseDto(2, "Item 2"));
        bookingResponseDto2.setStart(LocalDateTime.now().plusDays(20)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setEnd(LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setStatus(BookingStatus.REJECTED);

        when(service.getBookingToUser(anyInt(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    List<BookingResponseDto> bookingsResponse;
                    if (invocationOnMock.getArgument(3, Integer.class) == Integer.MAX_VALUE) {
                        bookingsResponse = List.of(bookingResponseDto1, bookingResponseDto2);
                    } else {
                        bookingsResponse = List.of(bookingResponseDto1);
                    }
                    return bookingsResponse;
                });

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].id", is(bookingResponseDto1.getId()),
                        Integer.class))
                .andExpect(jsonPath("$.[0].end", is(bookingResponseDto1.getEnd())))
                .andExpect(jsonPath("$.[0].start", is(bookingResponseDto1.getStart())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingResponseDto1.getItem().getId()),
                        Integer.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingResponseDto1.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingResponseDto1.getBooker().getId()),
                        Integer.class))
                .andExpect(jsonPath("$.[0].status", is(bookingResponseDto1.getStatus().toString())))
                .andExpect(jsonPath("$.[1].id", is(bookingResponseDto2.getId()), Integer.class))
                .andExpect(jsonPath("$.[1].end", is(bookingResponseDto2.getEnd())))
                .andExpect(jsonPath("$.[1].start", is(bookingResponseDto2.getStart())))
                .andExpect(jsonPath("$.[1].item.id", is(bookingResponseDto2.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.[1].item.name", is(bookingResponseDto2.getItem().getName())))
                .andExpect(jsonPath("$.[1].booker.id", is(bookingResponseDto2.getBooker().getId()),
                        Integer.class))
                .andExpect(jsonPath("$.[1].status", is(bookingResponseDto2.getStatus().toString())));
    }

    @Test
    void getBookingsToBookerWithSizeOneShouldBeOk() throws Exception {
        BookingResponseDto bookingResponseDto1 = new BookingResponseDto();
        bookingResponseDto1.setBooker(new BookerDto(1));
        bookingResponseDto1.setId(1);
        bookingResponseDto1.setItem(new ItemBookingResponseDto(1, "Item 1"));
        bookingResponseDto1.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setStatus(BookingStatus.APPROVED);
        BookingResponseDto bookingResponseDto2 = new BookingResponseDto();
        bookingResponseDto2.setBooker(new BookerDto(1));
        bookingResponseDto2.setId(2);
        bookingResponseDto2.setItem(new ItemBookingResponseDto(2, "Item 2"));
        bookingResponseDto2.setStart(LocalDateTime.now().plusDays(20)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setEnd(LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setStatus(BookingStatus.REJECTED);

        when(service.getBookingToUser(anyInt(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    List<BookingResponseDto> bookingsResponse;
                    if (invocationOnMock.getArgument(3, Integer.class) == null) {
                        bookingsResponse = List.of(bookingResponseDto1, bookingResponseDto2);
                    } else {
                        bookingsResponse = List.of(bookingResponseDto1);
                    }
                    return bookingsResponse;
                });

        mvc.perform(get("/bookings?from=0&size=1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(bookingResponseDto1.getId()),
                        Integer.class))
                .andExpect(jsonPath("$.[0].end", is(bookingResponseDto1.getEnd())))
                .andExpect(jsonPath("$.[0].start", is(bookingResponseDto1.getStart())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingResponseDto1.getItem().getId()),
                        Integer.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingResponseDto1.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingResponseDto1.getBooker().getId()),
                        Integer.class))
                .andExpect(jsonPath("$.[0].status", is(bookingResponseDto1.getStatus().toString())));
    }

    @Test
    void getBookingsToBookerShouldThrowsValidationExceptionWithInvalidFrom() throws Exception {
        mvc.perform(get("/bookings?from=-1&size=1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsToBookerShouldThrowsValidationExceptionWithInvalidSize() throws Exception {
        mvc.perform(get("/bookings?from=0&size=-1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsToBookerShouldThrowValidationExceptionWithInvalidUserIdInHeader() throws Exception {
        mvc.perform(get("/bookings?from=0&size=1")
                        .header("X-Sharer-User-Id", 0)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsToBookerShouldThrowValidationExceptionWithoutUserIdInHeader() throws Exception {
        mvc.perform(get("/bookings?from=0&size=1")
                        .header("X-Sharer-User-Id", "")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }


    @Test
    void getBookingsToOwnerWithNullSizeShouldBeOk() throws Exception {
        BookingResponseDto bookingResponseDto1 = new BookingResponseDto();
        bookingResponseDto1.setBooker(new BookerDto(1));
        bookingResponseDto1.setId(1);
        bookingResponseDto1.setItem(new ItemBookingResponseDto(1, "Item 1"));
        bookingResponseDto1.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setStatus(BookingStatus.APPROVED);
        BookingResponseDto bookingResponseDto2 = new BookingResponseDto();
        bookingResponseDto2.setBooker(new BookerDto(1));
        bookingResponseDto2.setId(2);
        bookingResponseDto2.setItem(new ItemBookingResponseDto(2, "Item 2"));
        bookingResponseDto2.setStart(LocalDateTime.now().plusDays(20)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setEnd(LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setStatus(BookingStatus.REJECTED);

        when(service.getBookingToOwner(anyInt(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    List<BookingResponseDto> bookingsResponse;
                    if (invocationOnMock.getArgument(3, Integer.class) == Integer.MAX_VALUE) {
                        bookingsResponse = List.of(bookingResponseDto1, bookingResponseDto2);
                    } else {
                        bookingsResponse = List.of(bookingResponseDto1);
                    }
                    return bookingsResponse;
                });

        mvc.perform(get("/bookings/owner?state=ALL")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].id", is(bookingResponseDto1.getId()),
                        Integer.class))
                .andExpect(jsonPath("$.[0].end", is(bookingResponseDto1.getEnd())))
                .andExpect(jsonPath("$.[0].start", is(bookingResponseDto1.getStart())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingResponseDto1.getItem().getId()),
                        Integer.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingResponseDto1.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingResponseDto1.getBooker().getId()),
                        Integer.class))
                .andExpect(jsonPath("$.[0].status", is(bookingResponseDto1.getStatus().toString())))
                .andExpect(jsonPath("$.[1].id", is(bookingResponseDto2.getId()), Integer.class))
                .andExpect(jsonPath("$.[1].end", is(bookingResponseDto2.getEnd())))
                .andExpect(jsonPath("$.[1].start", is(bookingResponseDto2.getStart())))
                .andExpect(jsonPath("$.[1].item.id", is(bookingResponseDto2.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.[1].item.name", is(bookingResponseDto2.getItem().getName())))
                .andExpect(jsonPath("$.[1].booker.id", is(bookingResponseDto2.getBooker().getId()),
                        Integer.class))
                .andExpect(jsonPath("$.[1].status", is(bookingResponseDto2.getStatus().toString())));
    }

    @Test
    void getBookingsToOwnerWithSizeOneShouldBeOk() throws Exception {
        BookingResponseDto bookingResponseDto1 = new BookingResponseDto();
        bookingResponseDto1.setBooker(new BookerDto(1));
        bookingResponseDto1.setId(1);
        bookingResponseDto1.setItem(new ItemBookingResponseDto(1, "Item 1"));
        bookingResponseDto1.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setStatus(BookingStatus.APPROVED);
        BookingResponseDto bookingResponseDto2 = new BookingResponseDto();
        bookingResponseDto2.setBooker(new BookerDto(1));
        bookingResponseDto2.setId(2);
        bookingResponseDto2.setItem(new ItemBookingResponseDto(2, "Item 2"));
        bookingResponseDto2.setStart(LocalDateTime.now().plusDays(20)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setEnd(LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setStatus(BookingStatus.REJECTED);

        when(service.getBookingToOwner(anyInt(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    List<BookingResponseDto> bookingsResponse;
                    if (invocationOnMock.getArgument(3, Integer.class) == null) {
                        bookingsResponse = List.of(bookingResponseDto1, bookingResponseDto2);
                    } else {
                        bookingsResponse = List.of(bookingResponseDto1);
                    }
                    return bookingsResponse;
                });

        mvc.perform(get("/bookings/owner?from=0&size=1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(bookingResponseDto1.getId()),
                        Integer.class))
                .andExpect(jsonPath("$.[0].end", is(bookingResponseDto1.getEnd())))
                .andExpect(jsonPath("$.[0].start", is(bookingResponseDto1.getStart())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingResponseDto1.getItem().getId()),
                        Integer.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingResponseDto1.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingResponseDto1.getBooker().getId()),
                        Integer.class))
                .andExpect(jsonPath("$.[0].status", is(bookingResponseDto1.getStatus().toString())));
    }

    @Test
    void getBookingsToOwnerShouldThrowsValidationExceptionWithInvalidFrom() throws Exception {
        mvc.perform(get("/bookings/owner?from=-1&size=1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsToOwnerShouldThrowsValidationExceptionWithInvalidSize() throws Exception {
        mvc.perform(get("/bookings/owner?from=0&size=-1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsToOwnerShouldThrowValidationExceptionWithInvalidUserIdInHeader() throws Exception {
        mvc.perform(get("/bookings/owner?from=0&size=1")
                        .header("X-Sharer-User-Id", 0)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsToOwnerShouldThrowValidationExceptionWithoutUserIdInHeader() throws Exception {
        mvc.perform(get("/bookings/owner?from=0&size=1")
                        .header("X-Sharer-User-Id", "")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}
