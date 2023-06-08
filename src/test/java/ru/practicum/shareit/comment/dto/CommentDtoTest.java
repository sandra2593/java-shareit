package ru.practicum.shareit.comment.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    private static Validator validator;
    @Autowired
    private JacksonTester<CommentDto> json;

    @BeforeAll
    public static void beforeAll() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testCommentDto() throws IOException {
        CommentDto commentDto = CommentDto.builder().id(1).text("comment").authorName("author")
                .created(LocalDateTime.of(2023, 6, 5, 11, 0)).build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("author");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-06-05T11:00:00");
    }

    @Test
    void testCommentDtoEmptyText() throws IOException {
        CommentDto commentDto = CommentDto.builder().id(1).authorName("author")
                .created(LocalDateTime.of(2023, 6, 5, 11, 0)).build();


        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);

        ConstraintViolation<CommentDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("text");
        assertThat(violation.getMessage()).isEqualTo("Текст комментария не должен быть пустым.");
    }
}
