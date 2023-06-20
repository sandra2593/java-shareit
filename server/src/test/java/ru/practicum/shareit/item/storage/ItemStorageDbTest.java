package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemStorageDbTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private ItemStorageDb itemStorage;

    private User owner;

    private Item item;

    private Item itemNotAvail;

    private Item item2;

    private Item item3;

    private Item item4;

    private Request request;

    @BeforeEach
    public void beforeEach() {
        User requestor = new User();
        requestor.setName("user");
        requestor.setEmail("user@mail.com");

        owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.com");

        request = new Request();
        request.setDescription("description");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.of(2023, 6, 5, 11, 0));

        item = new Item();
        item.setName("Дрель");
        item.setDescription("С Аккумулятором");
        item.setAvailable(true);
        item.setOwner(owner);

        itemNotAvail = new Item();
        itemNotAvail.setName("Дрель");
        itemNotAvail.setDescription("Обычная");
        itemNotAvail.setAvailable(false);
        itemNotAvail.setOwner(owner);

        item2 = new Item();
        item2.setName("Ключ");
        item2.setDescription("Гаечный");
        item2.setAvailable(true);
        item2.setOwner(owner);

        item3 = new Item();
        item3.setName("Ключ");
        item3.setDescription("Старый");
        item3.setAvailable(true);
        item3.setOwner(owner);

        item4 = new Item();
        item4.setName("Ключ");
        item4.setDescription("Не ключ");
        item4.setAvailable(false);
        item4.setOwner(owner);

        em.persist(owner);
        em.persist(requestor);
        em.persist(request);
        em.flush();

    }

    @Test
    void testCreate() {
        Item item = new Item();
        item.setName("Дрель c Запросом");
        item.setDescription("С Аккумулятором и Запросом");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);

        itemStorage.save(item);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item itemFromDb = query.setParameter("id", 1).getSingleResult();

        assertThat(itemFromDb.getId()).isEqualTo(1);
        assertThat(itemFromDb.getName()).isEqualTo(item.getName());
        assertThat(itemFromDb.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemFromDb.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(itemFromDb.getOwner()).isEqualTo(item.getOwner());
        assertThat(itemFromDb.getRequest()).isEqualTo(item.getRequest());
    }

    @Test
    void testSearchItemsFindOne() {
        em.persist(item);
        em.persist(itemNotAvail);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4);
        em.flush();

        List<Item> foundItems = itemStorage.searchItems("дрель", PageRequest.of(0, 2000));

        assertThat(foundItems).hasSize(1);
        assertThat(foundItems).containsAll(List.of(item));
        assertThat(foundItems).doesNotContain(itemNotAvail);
    }

    @Test
    void testSearchItemsFindMany() {
        em.persist(item);
        em.persist(itemNotAvail);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4);
        em.flush();

        List<Item> foundItems = itemStorage.searchItems("ключ", PageRequest.of(0, 2000));

        assertThat(foundItems).hasSize(2);
        assertThat(foundItems).containsAll(List.of(item2, item3));
        assertThat(foundItems).doesNotContain(item4);
    }

    @Test
    void testSearchFindByDescription() {
        em.persist(item);
        em.persist(itemNotAvail);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4);
        em.flush();

        List<Item> foundItems = itemStorage.searchItems("старый", PageRequest.of(0, 2000));

        assertThat(foundItems).hasSize(1);
        assertThat(foundItems).containsAll(List.of(item3));
        assertThat(foundItems).doesNotContain(item4);
    }

    @Test
    void testSearchFindNothing() {
        em.persist(item);
        em.persist(itemNotAvail);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4);
        em.flush();

        List<Item> foundItems = itemStorage.searchItems("мышка", PageRequest.of(0, 2000));

        assertThat(foundItems).isEmpty();
    }
}
