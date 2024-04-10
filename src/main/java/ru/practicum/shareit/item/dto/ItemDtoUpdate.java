package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Size;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoUpdate {
    private Integer id;

    @Size(max = 50)
    private String name;

    @Size(max = 250)
    private String description;

    private Boolean available;

    private User owner;
}
