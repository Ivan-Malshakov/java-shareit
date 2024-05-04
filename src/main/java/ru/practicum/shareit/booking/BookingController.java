package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResearchDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto saveBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                          @RequestBody @Valid BookingResearchDto bookingDto) {
        log.info("Save new booking {}", bookingDto);
        return bookingService.saveBooking(bookingDto, userId);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingResponseDto approvedOrRejectBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                                      @PathVariable @Min(1) Integer bookingId,
                                                      @RequestParam boolean approved) {
        log.info("Confirmation or rejection of a booking request with id = {}", bookingId);
        return bookingService.approvedOrRejectBooking(userId, bookingId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                         @PathVariable @Min(1) Integer bookingId) {
        log.info("Get bookings with id = {}", bookingId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsToBooker(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                        @RequestParam(required = false) @Min(1) Integer size) {
        log.info("Get bookings for user with id = {}", userId);
        if (size == null) {
            return bookingService.getBookingToUser(userId, state, from, Integer.MAX_VALUE);
        } else {
            return bookingService.getBookingToUser(userId, state, from, size);
        }
    }

    @GetMapping(value = "/owner")
    public List<BookingResponseDto> getBookingsToOwner(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                       @RequestParam(required = false) @Min(1) Integer size) {
        log.info("Get bookings for user with id = {}", userId);
        if (size == null) {
            return bookingService.getBookingToOwner(userId, state, from, Integer.MAX_VALUE);
        } else {
            return bookingService.getBookingToOwner(userId, state, from, size);
        }
    }
}
