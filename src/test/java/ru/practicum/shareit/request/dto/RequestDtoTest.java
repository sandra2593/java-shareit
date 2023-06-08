package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
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
public class RequestDtoTest {
    private static UserDtoShort userDtoShort;
    private static Validator validator;
    @Autowired
    private JacksonTester<RequestDto> json;

    @BeforeAll
    public static void beforeAll() {
        userDtoShort = UserDtoShort.builder().id(2).name("user").build();

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testRequestDto() throws IOException {
        RequestDto itemRequestDto = RequestDto.builder()
                .id(1)
                .description("description")
                .requestor(userDtoShort)
                .created(LocalDateTime.of(2023, 6, 5, 11, 0))
                .build();

        JsonContent<RequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.requestor.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-06-05T11:00:00");
    }

    @Test
    void testRequestDtoEmptyDescription() throws IOException {
        RequestDto requestDto = RequestDto.builder().id(1).requestor(userDtoShort)
                .created(LocalDateTime.of(2023, 6, 5, 11, 0)).build();

        Set<ConstraintViolation<RequestDto>> violations = validator.validate(requestDto);

        ConstraintViolation<RequestDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("Описание запроса не пустое");
    }
}
