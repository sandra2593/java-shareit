package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.storage.BookingStorageDb;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class BookingService implements BookingServiceInterface {

    private final BookingStorageDb bookingStorage;
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public BookingService(BookingStorageDb bookingStorage, ItemService itemService, UserService userService) {
        this.bookingStorage = bookingStorage;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Booking create(int userId, BookingDtoShort newBooking) {
        if (!isValidPeriod(newBooking)) {
            throw new ValidationException("период бронирования не валидный");
        }
        Item itemToBook = itemService.getItemById(newBooking.getItemId());
        if (itemToBook.getOwner().getId() == userId) {
            throw new BookerIsOwnerException("пользователь с id " + userId + " является владельцем товара с id " + itemToBook.getId());
        }
        if (!itemToBook.getAvailable()) {
            throw new UnavailableItemException("не доступен товар с id " + itemToBook.getId());
        }

        Booking booking = new Booking();
        booking.setStart(newBooking.getStart());
        booking.setEnd(newBooking.getEnd());
        booking.setItem(itemToBook);
        booking.setBooker(userService.getUserById(userId));
        booking.setStatus(BookingState.WAITING);

        return bookingStorage.save(booking);
    }

    @Override
    @Transactional
    public Booking approve(int bookingId, int userId, boolean isApproved) {
        User owner = userService.getUserById(userId);
        Booking bookingToApprove = getBookingById(bookingId, owner.getId());

        if (bookingToApprove.getItem().getOwner().getId() != owner.getId()) {
            throw new CannotApproveException("нет возможности подтвердить бюронирование " + bookingToApprove.getItem().getId());
        }
        if ((bookingToApprove.getStatus().equals(BookingState.APPROVED) && isApproved) ||
                (bookingToApprove.getStatus().equals(BookingState.REJECTED) && !isApproved)) {
            throw new SameApproveStatusException("одинаковый статус " + bookingToApprove.getStatus());
        }

        if (!isApproved) {
            bookingToApprove.setStatus(BookingState.REJECTED);
        } else {
            bookingToApprove.setStatus(BookingState.APPROVED);
        }

        return bookingStorage.save(bookingToApprove);
    }

    @Override
    public Booking getBookingById(int bookingId, int userId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(() -> new NotFoundException("нет букинга с id " + bookingId));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NoAccessToBookException("пользователь с id " + userId + " не имеет доступа к " + booking.getId());
        }

        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Booking> getUserBookings(int userId, BookingState state, Pageable pageable) {
        User user = userService.getUserById(userId);

        if (state.equals(BookingState.ALL)) {
            return bookingStorage.findBookingsByBookerOrderByStartDesc(user, pageable);
        }
        if (state.equals(BookingState.FUTURE)) {
            return bookingStorage.findBookingsByBookerAndStartAfterOrderByStartDesc(user, LocalDateTime.now(), pageable);
        }
        if (state.equals(BookingState.PAST)) {
            return bookingStorage.findBookingsByBookerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now(), pageable);
        }
        if (state.equals(BookingState.CURRENT)) {
            return bookingStorage.findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user, LocalDateTime.now(), LocalDateTime.now(), pageable);
        }

        return bookingStorage.findBookingsByBookerAndStatusOrderByStartDesc(user, state, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Booking> getOwnedItemsBookings(int ownerId, BookingState state, Pageable pageable) {
        Collection<Item> items = itemService.getUsersItems(ownerId);

        if (state.equals(BookingState.ALL)) {
            return bookingStorage.findBookingsByItemInOrderByStartDesc(items, pageable);
        }
        if (state.equals(BookingState.FUTURE)) {
            return bookingStorage.findBookingsByItemInAndStartAfterOrderByStartDesc(items, LocalDateTime.now(), pageable);
        }
        if (state.equals(BookingState.PAST)) {
            return bookingStorage.findBookingsByItemInAndEndBeforeOrderByStartDesc(items, LocalDateTime.now(), pageable);
        }
        if (state.equals(BookingState.CURRENT)) {
            return bookingStorage.findBookingsByItemInAndStartBeforeAndEndAfterOrderByStartDesc(items, LocalDateTime.now(), LocalDateTime.now(), pageable);
        }

        return bookingStorage.findBookingsByItemInAndStatusOrderByStartDesc(items, state, pageable);
    }

    private boolean isValidPeriod(BookingDtoShort bookingDto) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        return !start.equals(end) && !start.isAfter(end) && !end.isBefore(start) && !end.isBefore(LocalDateTime.now()) && !start.isBefore(LocalDateTime.now());
    }
}