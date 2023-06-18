package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestStorageDbTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private RequestStorageDb requestStorage;

    private User requestor;

    @BeforeEach
    void beforeEach() {
        requestor = new User();
        requestor.setName("user");
        requestor.setEmail("user@mail.com");

        em.persist(requestor);
        em.flush();
    }

    @Test
    void testCreate() {
        Request request = new Request();
        request.setDescription("description");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.of(2023, 6, 5, 11, 0));

        requestStorage.save(request);

        TypedQuery<Request> query = em.createQuery("Select ir from Request ir where ir.id = :id", Request.class);
        Request itemRequestFromDb = query.setParameter("id", 1).getSingleResult();

        assertThat(itemRequestFromDb.getId()).isEqualTo(1);
        assertThat(itemRequestFromDb.getDescription()).isEqualTo(request.getDescription());
        assertThat(itemRequestFromDb.getRequestor()).isEqualTo(request.getRequestor());
        assertThat(itemRequestFromDb.getCreated()).isEqualTo(request.getCreated());
    }

    @Test
    void testCreateNullDescription() {
        Request request = new Request();
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.of(2023, 6, 5, 11, 0));

        assertThatThrownBy(() -> requestStorage.save(request)).isInstanceOf(DataIntegrityViolationException.class);
    }
}
