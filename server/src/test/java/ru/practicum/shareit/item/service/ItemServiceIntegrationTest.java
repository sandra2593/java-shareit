package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {
    private final EntityManager em;
    private final ItemService itemService;
    private Item item;
    private Item itemNoBookingsAndComments;
    private Booking lastBooking;
    private Booking nextBooking;

    @BeforeEach
    void beforeEach() {
        User booker = new User();
        booker.setName("user");
        booker.setEmail("user@mail.com");

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.com");

        item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);

        itemNoBookingsAndComments = new Item();
        itemNoBookingsAndComments.setName("itemNoBookings");
        itemNoBookingsAndComments.setDescription("description");
        itemNoBookingsAndComments.setAvailable(true);
        itemNoBookingsAndComments.setOwner(booker);

        lastBooking = new Booking();
        lastBooking.setStart(LocalDateTime.now().minusDays(7));
        lastBooking.setEnd(LocalDateTime.now().minusDays(2));
        lastBooking.setItem(item);
        lastBooking.setBooker(booker);
        lastBooking.setStatus(BookingStatus.WAITING);

        nextBooking = new Booking();
        nextBooking.setStart(LocalDateTime.now().plusDays(4));
        nextBooking.setEnd(LocalDateTime.now().plusDays(5));
        nextBooking.setItem(item);
        nextBooking.setBooker(booker);
        nextBooking.setStatus(BookingStatus.WAITING);

        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.persist(itemNoBookingsAndComments);
        em.persist(lastBooking);
        em.persist(nextBooking);
        em.flush();
    }

    @Test
    void testGetUserCurtainItemWithBookingIntervals() {
        ItemDto itemDto = itemService.getItemByIdWithBookingIntervals(1, 1);

        assertThat(itemDto).isNotNull();

        assertThat(itemDto.getId()).isEqualTo(1);
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(itemDto.getLastBooking()).isEqualTo(BookingMapper.toBookingTimeIntervalDto(lastBooking));
        assertThat(itemDto.getNextBooking()).isEqualTo(BookingMapper.toBookingTimeIntervalDto(nextBooking));
    }

    @Test
    void testGetUserItemsWithBookingIntervals() {
        ArrayList<ItemDto> result = new ArrayList<>(itemService.getUserItemsWithBookingIntervals(1));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);

        ItemDto itemDto = result.get(0);
        assertThat(itemDto.getId()).isEqualTo(1);
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(itemDto.getLastBooking()).isEqualTo(BookingMapper.toBookingTimeIntervalDto(lastBooking));
        assertThat(itemDto.getNextBooking()).isEqualTo(BookingMapper.toBookingTimeIntervalDto(nextBooking));
    }

    @Test
    void testGetUserItemsNoBookingIntervals() {
        ArrayList<ItemDto> result = new ArrayList<>(itemService.getUserItemsWithBookingIntervals(2));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);

        ItemDto itemDto = result.get(0);
        assertThat(itemDto.getId()).isEqualTo(2);
        assertThat(itemDto.getName()).isEqualTo(itemNoBookingsAndComments.getName());
        assertThat(itemDto.getDescription()).isEqualTo(itemNoBookingsAndComments.getDescription());
        assertThat(itemDto.getAvailable()).isEqualTo(itemNoBookingsAndComments.getAvailable());
        assertThat(itemDto.getLastBooking()).isNull();
        assertThat(itemDto.getNextBooking()).isNull();
        assertThat(itemDto.getRequestId()).isNull();
    }
}
