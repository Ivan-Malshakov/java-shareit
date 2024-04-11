package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingResearchDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto saveBooking(BookingResearchDto bookingDto, Integer userId);

    BookingResponseDto approvedOrRejectBooking(Integer userId, Integer bookingId, Boolean approved);

    BookingResponseDto getBooking(Integer userId, Integer bookingId);

    List<BookingResponseDto> getBookingToUser(Integer userId, String state);

    List<BookingResponseDto> getBookingToOwner(Integer userId, String state);
}
