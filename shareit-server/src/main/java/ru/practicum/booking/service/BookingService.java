package ru.practicum.booking.service;

import ru.practicum.booking.dto.BookingResearchDto;
import ru.practicum.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto saveBooking(BookingResearchDto bookingDto, Integer userId);

    BookingResponseDto approvedOrRejectBooking(Integer userId, Integer bookingId, Boolean approved);

    BookingResponseDto getBooking(Integer userId, Integer bookingId);

    List<BookingResponseDto> getBookingToUser(Integer userId, String state, Integer from, Integer size);

    List<BookingResponseDto> getBookingToOwner(Integer userId, String state, Integer from, Integer size);
}
