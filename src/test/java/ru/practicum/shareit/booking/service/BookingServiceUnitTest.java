package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(2);
    @Mock
    private BookingStorageDb bookingStorage;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @InjectMocks
    private BookingService bookingService;

    private User owner;

    private User booker;

    private Item item;

    private Booking bookingToApprove;

    private Booking approvedBooking;

    private static Booking copyBooking(Booking originalBooking) {
        Booking copiedBooking = new Booking();
        copiedBooking.setId(originalBooking.getId());
        copiedBooking.setStart(originalBooking.getStart());
        copiedBooking.setEnd(originalBooking.getEnd());
        copiedBooking.setItem(originalBooking.getItem());
        copiedBooking.setBooker(originalBooking.getBooker());
        copiedBooking.setStatus(originalBooking.getStatus());

        return copiedBooking;
    }

    @BeforeEach
    void beforeEach() {
        booker = new User();
        booker.setId(25);
        booker.setName("user");
        booker.setEmail("user@mail.com");

        owner = new User();
        owner.setId(10);
        owner.setName("owner");
        owner.setEmail("owner@mail.com");

        item = new Item();
        item.setId(10);
        item.setName("item");
        item.setAvailable(true);
        item.setOwner(owner);

        bookingToApprove = new Booking();
        bookingToApprove.setId(1);
        bookingToApprove.setStart(start);
        bookingToApprove.setEnd(end);
        bookingToApprove.setItem(item);
        bookingToApprove.setBooker(booker);
        bookingToApprove.setStatus(BookingState.WAITING);

        approvedBooking = copyBooking(bookingToApprove);
        approvedBooking.setStatus(BookingState.APPROVED);

    }

    @Test
    void testCreateBooking() {
        Booking correctBooking = copyBooking(bookingToApprove);

        BookingDtoShort createBookingDto = BookingDtoShort.builder().start(start).end(end).itemId(correctBooking.getItem().getId()).build();

        when(itemService.getItemById(anyInt())).thenReturn(item);
        when(userService.getUserById(anyInt())).thenReturn(booker);
        when(bookingStorage.save(any(Booking.class))).thenReturn(correctBooking);

        Booking createdBooking = bookingService.create(booker.getId(), createBookingDto);

        assertThat(createdBooking).usingRecursiveComparison().isEqualTo(correctBooking);
    }

    @Test
    void testCreateBookingInvalidBookingPeriod() {
        BookingDtoShort createBookingDto = BookingDtoShort.builder().start(end).end(start).itemId(item.getId()).build();

        assertThatThrownBy(() -> bookingService.create(booker.getId(), createBookingDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("период бронирования не валидный");
    }

    @Test
    void testCreateBookingItemIsNotAvailable() {
        BookingDtoShort createBookingDto = BookingDtoShort.builder().start(start).end(end).itemId(item.getId()).build();

        item.setAvailable(false);

        when(itemService.getItemById(anyInt())).thenReturn(item);

        assertThatThrownBy(() -> bookingService.create(booker.getId(), createBookingDto))
                .isInstanceOf(UnavailableItemException.class)
                .hasMessageContaining("не доступен товар с id 10");
    }

    @Test
    void testCreateBookingItemOwnerCannotBook() {
        BookingDtoShort createBookingDto = BookingDtoShort.builder().start(start).end(end).itemId(item.getId()).build();

        when(itemService.getItemById(anyInt())).thenReturn(item);

        assertThatThrownBy(() -> bookingService.create(owner.getId(), createBookingDto))
                .isInstanceOf(BookerIsOwnerException.class)
                .hasMessageContaining("пользователь с id 10 является владельцем товара с id 10");
    }

    @Test
    void testApproveBooking() {
        when(userService.getUserById(anyInt())).thenReturn(owner);
        when(bookingStorage.findById(anyInt())).thenReturn(Optional.of(bookingToApprove));
        when(bookingStorage.save(any(Booking.class))).thenReturn(approvedBooking);

        Booking resultBooking = bookingService.approve(bookingToApprove.getId(), owner.getId(), true);

        assertThat(resultBooking).usingRecursiveComparison().isEqualTo(approvedBooking);
    }

    @Test
    void testRejectBooking() {
        Booking rejectedBooking = copyBooking(bookingToApprove);
        rejectedBooking.setStatus(BookingState.REJECTED);

        when(userService.getUserById(anyInt())).thenReturn(owner);
        when(bookingStorage.findById(anyInt())).thenReturn(Optional.of(bookingToApprove));
        when(bookingStorage.save(any(Booking.class))).thenReturn(rejectedBooking);

        Booking resultBooking = bookingService.approve(bookingToApprove.getId(), owner.getId(), false);

        assertThat(resultBooking).usingRecursiveComparison().isEqualTo(rejectedBooking);
    }

    @Test
    void testCannotApproveBookingNotOwner() {
        when(userService.getUserById(anyInt())).thenReturn(booker);
        when(bookingStorage.findById(anyInt())).thenReturn(Optional.of(bookingToApprove));

        assertThatThrownBy(() -> bookingService.approve(bookingToApprove.getId(), booker.getId(), false))
                .isInstanceOf(CannotApproveException.class)
                .hasMessageContaining("нет возможности подтвердить бюронирование 10");
    }

    @Test
    void testCannotApproveBookingUsingSameStatus() {
        when(userService.getUserById(anyInt())).thenReturn(owner);
        when(bookingStorage.findById(anyInt())).thenReturn(Optional.of(approvedBooking));

        assertThatThrownBy(() -> bookingService.approve(approvedBooking.getId(), owner.getId(), true))
                .isInstanceOf(SameApproveStatusException.class)
                .hasMessageContaining("одинаковый статус APPROVED");
    }

    @Test
    void testCannotGetBookingByIdNotOwner() {
        Booking bookingToGet = copyBooking(bookingToApprove);

        when(bookingStorage.findById(anyInt())).thenReturn(Optional.of(bookingToGet));

        assertThatThrownBy(() -> bookingService.getBookingById(1, bookingToGet.getId()))
                .isInstanceOf(NoAccessToBookException.class)
                .hasMessageContaining("пользователь с id 1 не имеет доступа к 1");
    }

    @Test
    void testGetUserBookingsStatusAll() {
        when(userService.getUserById(anyInt())).thenReturn(booker);

        BookingState bookingState = BookingState.ALL;
        when(bookingStorage.findBookingsByBookerOrderByStartDesc(any(User.class), any(Pageable.class))).thenReturn(List.of(bookingToApprove));
        Collection<Booking> bookingsALL = bookingService.getUserBookings(booker.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsALL).contains(bookingToApprove);
    }

    @Test
    void testGetUserBookingsStatusFuture() {
        when(userService.getUserById(anyInt())).thenReturn(booker);

        BookingState bookingState = BookingState.FUTURE;
        Booking futureBooking = copyBooking(bookingToApprove);
        futureBooking.setStart(LocalDateTime.now().plusDays(10));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        when(bookingStorage.findBookingsByBookerAndStartAfterOrderByStartDesc(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(futureBooking));
        Collection<Booking> bookingsFUTURE = bookingService.getUserBookings(booker.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsFUTURE).contains(futureBooking);
    }

    @Test
    void testGetUserBookingsStatusPast() {
        when(userService.getUserById(anyInt())).thenReturn(booker);

        BookingState bookingState = BookingState.PAST;
        Booking pastBooking = copyBooking(bookingToApprove);
        pastBooking.setStart(LocalDateTime.now().minusDays(10));
        pastBooking.setEnd(LocalDateTime.now().minusDays(8));
        when(bookingStorage.findBookingsByBookerAndEndBeforeOrderByStartDesc(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(pastBooking));
        Collection<Booking> bookingsPAST = bookingService.getUserBookings(booker.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsPAST).contains(pastBooking);
    }

    @Test
    void testGetUserBookingsStatusCurrent() {
        when(userService.getUserById(anyInt())).thenReturn(booker);

        BookingState bookingState = BookingState.CURRENT;
        Booking currentBooking = copyBooking(bookingToApprove);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(2));
        when(bookingStorage.findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(currentBooking));
        Collection<Booking> bookingsCURRENT = bookingService.getUserBookings(booker.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsCURRENT).contains(currentBooking);
    }

    @Test
    void testGetUserBookingsStatusRejected() {
        when(userService.getUserById(anyInt())).thenReturn(booker);

        BookingState bookingState = BookingState.REJECTED;
        Booking rejectedBooking = copyBooking(bookingToApprove);
        rejectedBooking.setStart(LocalDateTime.now().minusDays(1));
        rejectedBooking.setEnd(LocalDateTime.now().plusDays(2));
        rejectedBooking.setStatus(BookingState.REJECTED);
        when(bookingStorage.findBookingsByBookerAndStatusOrderByStartDesc(any(User.class), any(BookingState.class), any(Pageable.class)))
                .thenReturn(List.of(rejectedBooking));
        Collection<Booking> bookingsREJECTED = bookingService.getUserBookings(booker.getId(), bookingState, PageRequest.of(0, 2000));
        assertThat(bookingsREJECTED).contains(rejectedBooking);
    }

}
