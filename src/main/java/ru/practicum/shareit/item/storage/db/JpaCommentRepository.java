package ru.practicum.shareit.item.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Comment;

import java.util.List;

public interface JpaCommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findCommentByItem_Id(Integer itemId);

}
