package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserStorageDbTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private UserStorageDb userStorage;

    @Test
    void testCreate() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@mail.com");

        userStorage.save(user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User userFromDb = query.setParameter("id", 1).getSingleResult();

        assertThat(userFromDb.getId()).isEqualTo(1);
        assertThat(userFromDb.getName()).isEqualTo(user.getName());
        assertThat(userFromDb.getEmail()).isEqualTo(user.getEmail());
    }
}
