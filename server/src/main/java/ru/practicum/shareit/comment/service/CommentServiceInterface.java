package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.model.Comment;

public interface CommentServiceInterface {
    Comment addComment(int itemId, int userId, Comment newComment);
}
