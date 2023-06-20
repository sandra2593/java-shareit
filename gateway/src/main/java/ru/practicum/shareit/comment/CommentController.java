package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.client.CommentClient;
import ru.practicum.shareit.comment.dto.CommentDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class CommentController {
    private final CommentClient commentClient;
    private static final String HEADER_PARAM = "X-Sharer-User-Id";

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable int itemId, @RequestHeader(value = HEADER_PARAM) int userId, @Valid @RequestBody CommentDto commentDto) {
        return commentClient.addComment(itemId, userId, commentDto);
    }
}
