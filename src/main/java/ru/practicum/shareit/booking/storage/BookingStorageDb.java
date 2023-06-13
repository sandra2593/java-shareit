package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingStorageDb extends JpaRepository<Booking, Integer> {
    List<Booking> findBookingsByBookerOrderByStartDesc(User user, Pageable pageable);

    List<Booking> findBookingsByBookerAndStatusOrderByStartDesc(User user, BookingState state, Pageable pageable);

    List<Booking> findBookingsByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime currentTime, Pageable pageable);

    List<Booking> findBookingsByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime currentTime, Pageable pageable);

    List<Booking> findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime currentTime1, LocalDateTime currentTime2, Pageable pageable);

    List<Booking> findBookingsByItemInOrderByStartDesc(Collection<Item> items, Pageable pageable);

    List<Booking> findBookingsByItemInAndStatusOrderByStartDesc(Collection<Item> items, BookingState state, Pageable pageable);

    List<Booking> findBookingsByItemInAndStartAfterOrderByStartDesc(Collection<Item> items, LocalDateTime currentTime, Pageable pageable);

    List<Booking> findBookingsByItemInAndEndBeforeOrderByStartDesc(Collection<Item> items, LocalDateTime currentTime, Pageable pageable);

    List<Booking> findBookingsByItemInAndStartBeforeAndEndAfterOrderByStartDesc(Collection<Item> items, LocalDateTime currentTime1, LocalDateTime currentTime2, Pageable pageable);

    @Query(value = "SELECT id,start_date,end_date,item_id,booker_id,status\n" +
            "FROM (SELECT b.id,\n" +
            "             b.start_date,\n" +
            "             b.end_date,\n" +
            "             b.booker_id,\n" +
            "             b.item_id,\n" +
            "             b.status,\n" +
            "             ROW_NUMBER() OVER (PARTITION BY b.item_id ORDER BY b.end_date DESC ) AS rn\n" +
            "      FROM bookings b\n" +
            "      WHERE b.item_id IN ?1\n" +
            "        AND (b.end_date < CURRENT_TIMESTAMP OR (b.start_date < CURRENT_TIMESTAMP AND b.end_date > CURRENT_TIMESTAMP))\n" +
            "        AND b.status != 'REJECTED') s\n" +
            "WHERE s.rn = 1", nativeQuery = true)
    List<Booking> getItemsLastBookings(Collection<Item> items);

    @Query(value = "SELECT id, start_date, end_date, item_id, booker_id, status\n" +
            "FROM (SELECT b.id,\n" +
            "             b.start_date,\n" +
            "             b.end_date,\n" +
            "             b.booker_id,\n" +
            "             b.item_id,\n" +
            "             b.status,\n" +
            "             ROW_NUMBER() OVER (PARTITION BY b.item_id ORDER BY b.start_date) AS rn\n" +
            "      FROM bookings b\n" +
            "      WHERE b.item_id IN ?1\n" +
            "        AND b.start_date > CURRENT_TIMESTAMP\n" +
            "        AND b.status != 'REJECTED') s\n" +
            "WHERE s.rn = 1", nativeQuery = true)
    List<Booking> getItemsNextBookings(Collection<Item> items);

    Collection<Booking> getBookingsByBookerAndItemAndEndIsBeforeAndStatus(User user, Item item, LocalDateTime currentTime, BookingState state);
}
