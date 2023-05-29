package ru.practicum.shareit.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    private long id;

    @NotBlank(message = "Текст комментария не должен быть пустым.")
    @Size(max = 1000, message = "Длина комментария не может превышать 1000 символов.")
    private String text;

    private String authorName;

    private LocalDateTime created;
}