package ru.practicum.item.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.item.Comment;

import java.util.List;

public interface JpaCommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findCommentByItem_Id(Integer itemId);

}
