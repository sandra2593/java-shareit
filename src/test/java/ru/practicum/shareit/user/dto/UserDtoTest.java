package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    private static Validator validator;
    @Autowired
    private JacksonTester<UserDto> json;

    @BeforeAll
    public static void beforeAll() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testUserDto() throws IOException {
        UserDto userDto = UserDto.builder().id(1).name("user").email("user@gmail.com").build();

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@gmail.com");
    }

    @Test
    void testUserDtoEmptyName() {
        UserDto userDto = UserDto.builder().id(1).email("user@gmail.com").build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<UserDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("Имя не должно быть пустым");
    }

    @Test
    void testUserDtoEmptyEmail() {
        UserDto userDto = UserDto.builder().id(1).name("user").build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        ConstraintViolation<UserDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
        assertThat(violation.getMessage()).isEqualTo("Email не должен быть пустым");
    }

    @Test
    void testUserDtoWrongEmail() {
        UserDto userDto = UserDto.builder().id(1).name("user").email("usergmail.com").build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        ConstraintViolation<UserDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
        assertThat(violation.getMessage()).isEqualTo("Некорректный формат почты");
    }
}
