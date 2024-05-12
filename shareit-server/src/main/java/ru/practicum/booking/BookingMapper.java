package ru.practicum.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.booking.dto.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = BookingMapper.class)
public interface BookingMapper {
    Booking toBooking(BookingResearchDto bookingResearchDto);

    BookingResponseDto toDto(Booking booking);

    List<BookingResponseDto> toListDto(List<Booking> bookings);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingResponseToItemDto toBookingToItem(Booking booking);
}
