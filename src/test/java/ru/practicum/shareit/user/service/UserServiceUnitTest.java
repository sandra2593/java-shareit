package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceUnitTest {
    private final EntityManager em;
    private final UserService userService;
    private User oldUser;

    @BeforeEach
    void beforeEach() {
        oldUser = new User();
        oldUser.setName("user");
        oldUser.setEmail("juser@mail.com");

        em.persist(oldUser);
        em.flush();
    }

    @Test
    void testUpdate() {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setName("updatedUser");
        updatedUser.setEmail("updatedUser@mail.com");

        userService.update(1, updatedUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", updatedUser.getId()).getSingleResult();

        assertThat(user.getId()).isEqualTo(updatedUser.getId());
        assertThat(user.getName()).isEqualTo(updatedUser.getName());
        assertThat(user.getEmail()).isEqualTo(updatedUser.getEmail());
    }

    @Test
    void testUpdateOnlyEmail() {

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setEmail("updatedUser@mail.com");

        userService.update(1, updatedUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", updatedUser.getId()).getSingleResult();

        assertThat(user.getId()).isEqualTo(oldUser.getId());
        assertThat(user.getName()).isEqualTo(oldUser.getName());
        assertThat(user.getEmail()).isEqualTo(oldUser.getEmail());
    }

    @Test
    void testUpdateOnlyName() {

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setName("updatedUser");

        userService.update(1, updatedUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", updatedUser.getId()).getSingleResult();

        assertThat(user.getId()).isEqualTo(oldUser.getId());
        assertThat(user.getName()).isEqualTo(updatedUser.getName());
        assertThat(user.getEmail()).isEqualTo(oldUser.getEmail());
    }

    @Test
    void testUpdateUserNotExist() {
        User updatedUser = new User();
        updatedUser.setId(999);
        updatedUser.setName("user");
        updatedUser.setEmail("user@mail.com");

        assertThatThrownBy(() -> userService.update(999, updatedUser))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("нет пользователя с id 999");

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", oldUser.getId()).getSingleResult();

        assertThat(user.getId()).isEqualTo(oldUser.getId());
        assertThat(user.getName()).isEqualTo(oldUser.getName());
        assertThat(user.getEmail()).isEqualTo(oldUser.getEmail());
    }

    @Test
    void testGetUserByIdNotExist() {
        assertThatThrownBy(() -> userService.getUserById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("нет пользователя с id 999");
    }
}
