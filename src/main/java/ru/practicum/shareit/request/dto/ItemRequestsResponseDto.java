package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemToRequestResponse;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestsResponseDto {
    private Integer id;
    private String description;
    private LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    private List<ItemToRequestResponse> items = new ArrayList<>();
}
