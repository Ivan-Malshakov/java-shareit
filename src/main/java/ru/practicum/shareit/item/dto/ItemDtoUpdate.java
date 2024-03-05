package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.user.User;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoUpdate {
    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;
}
