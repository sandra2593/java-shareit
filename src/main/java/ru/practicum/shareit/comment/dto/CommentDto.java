package ru.practicum.shareit.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    int id;
    @NotBlank(message = "Текст комментария не должен быть пустым.")
    @Size(max = 1000, message = "Длина комментария не может превышать 1000 символов.")
    String text;
    String authorName;
    LocalDateTime created;
}
