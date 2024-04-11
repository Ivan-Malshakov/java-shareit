package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentResearchDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface CommentMapper {

    Comment toComment(CommentResearchDto commentResearchDto);

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentResponseDto toDto(Comment comment);

    List<CommentResponseDto> toListCommentDto(List<Comment> comments);
}
