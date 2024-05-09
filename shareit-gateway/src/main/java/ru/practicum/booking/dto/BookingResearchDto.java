package ru.practicum.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResearchDto {
    private Integer id;
    @NotNull
    private LocalDateTime end;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    private Integer itemId;
}
