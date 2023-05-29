package ru.practicum.shareit.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.service.CommentService;

import javax.validation.Valid;

@RestController
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/items/{itemId}/comment")
    public CommentDto addComment(@PathVariable int itemId, @RequestHeader(value = "X-Sharer-User-Id") int userId, @Valid @RequestBody CommentDto commentDto) {
        return CommentMapper.toCommentDto(commentService.addComment(itemId, userId, CommentMapper.fromCommentDto(commentDto)));
    }
}
