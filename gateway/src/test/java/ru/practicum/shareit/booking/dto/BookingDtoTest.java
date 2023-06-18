package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    private static ItemDtoShort itemDtoShort;
    private static UserDtoShort userDtoShort;
    private static Validator validator;
    @Autowired
    private JacksonTester<BookingDto> json;

    @BeforeAll
    public static void beforeAll() {
        itemDtoShort = ItemDtoShort.builder().id(10).name("item").build();
        userDtoShort = UserDtoShort.builder().id(11).name("user").build();

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testBookingDto() throws IOException {
        BookingDto bookingDto = BookingDto.builder().id(1).start(LocalDateTime.of(2023, 6, 5, 11, 0))
                .end(LocalDateTime.of(2023, 6, 6, 11, 0)).item(itemDtoShort).booker(userDtoShort).status(BookingState.WAITING).build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-06-05T11:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-06-06T11:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(11);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    void testBookingDtoEmptyStart() throws IOException {
        BookingDto bookingDto = BookingDto.builder().id(1).end(LocalDateTime.of(2023, 6, 5, 11, 0))
                .item(itemDtoShort).booker(userDtoShort).status(BookingState.WAITING).build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);

        ConstraintViolation<BookingDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("start");
        assertThat(violation.getMessage()).isEqualTo("Дата начала брони не пустая");
    }

    @Test
    void testBookingDtoEmptyEnd() throws IOException {
        BookingDto bookingDto = BookingDto.builder().id(1).start(LocalDateTime.of(2023, 6, 5, 11, 0))
                .item(itemDtoShort).booker(userDtoShort).status(BookingState.WAITING).build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);

        ConstraintViolation<BookingDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("end");
        assertThat(violation.getMessage()).isEqualTo("Дата окончания брони не пустая");
    }
}
