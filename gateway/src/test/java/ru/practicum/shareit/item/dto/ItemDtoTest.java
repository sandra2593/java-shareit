package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingTimeIntervalDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    private static BookingTimeIntervalDto bookingTimeIntervalDtoLast;
    private static BookingTimeIntervalDto bookingTimeIntervalDtoNext;
    private static CommentDto commentDto;
    private static Validator validator;
    @Autowired
    private JacksonTester<ItemDto> json;

    @BeforeAll
    public static void beforeAll() {
        bookingTimeIntervalDtoLast = BookingTimeIntervalDto.builder().id(10).start(LocalDateTime.of(2023, 5, 4, 11, 0))
                .end(LocalDateTime.of(2023, 5, 5, 11, 0)).bookerId(1).build();
        bookingTimeIntervalDtoNext = BookingTimeIntervalDto.builder().id(10).start(LocalDateTime.of(2023, 6, 4, 11, 0))
                .end(LocalDateTime.of(2023, 6, 5, 11, 0)).bookerId(1).build();
        commentDto = CommentDto.builder().id(100).text("comment").authorName("author")
                .created(LocalDateTime.of(2023, 5, 6, 11, 0)).build();

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testItemDto() throws IOException {
        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("item")
                .description("description")
                .available(true)
                .lastBooking(bookingTimeIntervalDtoLast)
                .nextBooking(bookingTimeIntervalDtoNext)
                .comments(List.of(commentDto))
                .requestId(150)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2023-05-04T11:00:00");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo("2023-05-05T11:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo("2023-06-04T11:00:00");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo("2023-06-05T11:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(100);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("author");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2023-05-06T11:00:00");
    }

    @Test
    void testItemDtoEmptyName() {
        ItemDto itemDto = ItemDto.builder().id(1).description("description").available(true).lastBooking(bookingTimeIntervalDtoLast)
                .nextBooking(bookingTimeIntervalDtoNext).comments(List.of(commentDto)).requestId(150).build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);

        ConstraintViolation<ItemDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("Название не должно быть пустым");
    }

    @Test
    void testItemDtoEmptyDescription() {
        ItemDto itemDto = ItemDto.builder().id(1).name("item").available(true).lastBooking(bookingTimeIntervalDtoLast)
                .nextBooking(bookingTimeIntervalDtoNext).comments(List.of(commentDto)).requestId(150).build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);

        ConstraintViolation<ItemDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("Описание не должно быть пустым");
    }

    @Test
    void testItemDtoEmptyAvailabilityStatus() {
        ItemDto itemDto = ItemDto.builder().id(1).name("item").description("description").lastBooking(bookingTimeIntervalDtoLast)
                .nextBooking(bookingTimeIntervalDtoNext).comments(List.of(commentDto)).requestId(150).build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);

        ConstraintViolation<ItemDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("available");
        assertThat(violation.getMessage()).isEqualTo("Доступность должена быть указана");
    }
}
