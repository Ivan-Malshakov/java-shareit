package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResearchDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.HashSet;
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
                                          @RequestBody @Valid BookingResearchDto bookingDto)
            throws ConstraintViolationException {
        if (userId < 1) {
            throw new ConstraintViolationException("Invalid userId", new HashSet<>());
        }

        log.info("Save new booking {}", bookingDto);
        return bookingService.saveBooking(bookingDto, userId);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingResponseDto approvedOrRejectBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                                      @PathVariable @Min(1) Integer bookingId,
                                                      @RequestParam boolean approved)
            throws ConstraintViolationException {
        if (userId < 1) {
            throw new ConstraintViolationException("Invalid userId", new HashSet<>());
        }
        if (bookingId < 1) {
            throw new ConstraintViolationException("Invalid bookingId", new HashSet<>());
        }

        log.info("Confirmation or rejection of a booking request with id = {}", bookingId);
        return bookingService.approvedOrRejectBooking(userId, bookingId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                         @PathVariable @Min(1) Integer bookingId)
            throws ConstraintViolationException {
        if (userId < 1) {
            throw new ConstraintViolationException("Invalid userId", new HashSet<>());
        }
        if (bookingId < 1) {
            throw new ConstraintViolationException("Invalid bookingId", new HashSet<>());
        }

        log.info("Get bookings with id = {}", bookingId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsToBooker(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                                        @RequestParam(defaultValue = "ALL") String state)
            throws ConstraintViolationException {
        if (userId < 1) {
            throw new ConstraintViolationException("Invalid userId", new HashSet<>());
        }

        log.info("Get bookings for user with id = {}", userId);
        return bookingService.getBookingToUser(userId, state);
    }

    @GetMapping(value = "/owner")
    public List<BookingResponseDto> getBookingsToOwner(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                                       @RequestParam(defaultValue = "ALL") String state)
            throws ConstraintViolationException {
        if (userId < 1) {
            throw new ConstraintViolationException("Invalid userId", new HashSet<>());
        }

        log.info("Get bookings for user with id = {}", userId);
        return bookingService.getBookingToOwner(userId, state);
    }
}
