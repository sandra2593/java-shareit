package ru.practicum.shareit.comment.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface CommentStorageDb extends JpaRepository<Comment, Integer> {
    @Query(value = "SELECT * FROM comments WHERE item_id IN ?1", nativeQuery = true)
    Collection<Comment> getCommentsByItems(Collection<Item> items);
}
