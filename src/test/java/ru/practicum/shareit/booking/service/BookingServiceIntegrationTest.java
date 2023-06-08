package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIntegrationTest {
    private final EntityManager em;
    private final BookingService bookingService;
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(2);
    private Item item;
    private User booker;

    @BeforeEach
    void beforeEach() {
        booker = new User();
        booker.setName("user");
        booker.setEmail("user@mail.com");

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.com");

        item = new Item();
        item.setName("Дрель не дрель");
        item.setDescription("С Аккумулятором");
        item.setAvailable(true);
        item.setOwner(owner);

        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.flush();
    }

    @Test
    void testCreate() {
        BookingDtoShort createBookingDto = BookingDtoShort.builder().start(start).end(end).itemId(1).build();

        bookingService.create(2, createBookingDto);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking createdBooking = query.setParameter("id", 1).getSingleResult();

        assertThat(createdBooking.getId()).isEqualTo(1);
        assertThat(createdBooking.getStart()).isEqualTo(start);
        assertThat(createdBooking.getEnd()).isEqualTo(end);
        assertThat(createdBooking.getItem()).usingRecursiveComparison().isEqualTo(item);
        assertThat(createdBooking.getBooker()).usingRecursiveComparison().isEqualTo(booker);
        assertThat(createdBooking.getStatus()).isEqualTo(BookingState.WAITING);
    }
}
