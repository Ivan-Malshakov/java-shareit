package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingResearchDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> saveBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                              @RequestBody @Valid BookingResearchDto bookingDto) {
        log.info("Save new booking {}", bookingDto);
        return bookingClient.saveBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedOrRejectBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                                          @PathVariable @Min(1) Integer bookingId,
                                                          @RequestParam boolean approved) {
        log.info("Confirmation or rejection of a booking request with id = {}", bookingId);
        return bookingClient.approvedOrRejectBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                             @PathVariable @Min(1) Integer bookingId) {
        log.info("Get bookings with id = {}", bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsToBooker(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                      @RequestParam(required = false) @Min(1) Integer size) {
        if (size == null) {
            size = Integer.MAX_VALUE;
        }
        log.info("Get bookings for user with id = {} " +
                "state:{} , from = {}, size = {}", userId, state, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsToOwner(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(required = false) @Min(1) Integer size) {
        if (size == null) {
            size = Integer.MAX_VALUE;
        }
        log.info("Get bookings for user with id = = {}" +
                " state:{} , from = {}, size = {}", userId, state, from, size);
        return bookingClient.getBookingsOwner(userId, state, from, size);
    }
}
