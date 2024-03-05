package ru.practicum.shareit.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"id"})
@SuperBuilder
@NoArgsConstructor
public class ItemRequest {
    private Integer id;

    @NotBlank
    @Size(max = 250)
    private String description;

    @NotNull
    private User requestor;

    @NotNull
    private LocalDateTime created;

}
