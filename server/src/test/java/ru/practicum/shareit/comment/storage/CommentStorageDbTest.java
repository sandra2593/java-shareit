package ru.practicum.shareit.comment.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentStorageDbTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private CommentStorageDb commentStorage;

    private User author;

    private Item item;

    @BeforeEach
    void beforeEach() {
        author = new User();
        author.setName("user");
        author.setEmail("user@mail.com");

        item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(author);

        em.persist(author);
        em.persist(item);
        em.flush();
    }

    @Test
    void testCreate() {
        Comment comment = new Comment();
        comment.setText("comment");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2023, 6, 5, 11, 0));

        commentStorage.save(comment);

        TypedQuery<Comment> query = em.createQuery("Select i from Comment i where i.id = :id", Comment.class);
        Comment commentFromDb = query.setParameter("id", 1).getSingleResult();

        assertThat(commentFromDb.getId()).isEqualTo(1);
        assertThat(commentFromDb.getText()).isEqualTo(comment.getText());
        assertThat(commentFromDb.getAuthor()).isEqualTo(comment.getAuthor());
        assertThat(commentFromDb.getItem()).isEqualTo(comment.getItem());
    }
}
