package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    @NotNull
    private Integer id;
    @NotNull
    private String text;
    @NotNull
    private String authorName;
    private LocalDateTime created;
}
