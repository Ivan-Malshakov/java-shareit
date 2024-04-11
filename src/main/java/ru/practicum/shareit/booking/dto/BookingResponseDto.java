package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.booking.BookingStatus;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private Integer id;
    private String end;
    private String start;
    private ItemBookingResponseDto item;
    private BookerDto booker;
    private BookingStatus status;
}
