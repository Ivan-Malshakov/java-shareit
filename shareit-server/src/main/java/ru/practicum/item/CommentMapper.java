package ru.practicum.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.item.dto.CommentResearchDto;
import ru.practicum.item.dto.CommentResponseDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface CommentMapper {

    Comment toComment(CommentResearchDto commentResearchDto);

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentResponseDto toDto(Comment comment);

    List<CommentResponseDto> toListCommentDto(List<Comment> comments);
}
