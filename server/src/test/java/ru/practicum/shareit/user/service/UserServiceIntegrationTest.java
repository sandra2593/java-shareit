package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {
    private final EntityManager em;

    private final UserService userService;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setName("user");
        user.setEmail("user@mail.com");

        em.persist(user);
        em.flush();
    }

    @Test
    void testGetAll() {
        UserDto correctUser = UserMapper.toUserDto(user);

        ArrayList<User> results = new ArrayList<>(userService.getAll());
        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(1);

        User result = results.get(0);
        assertThat(result.getId()).isEqualTo(correctUser.getId());
        assertThat(result.getName()).isEqualTo(correctUser.getName());
        assertThat(result.getEmail()).isEqualTo(correctUser.getEmail());
    }
}
