package ru.practicum.shareit.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorageDb;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentStorageDb;
import ru.practicum.shareit.exception.CannotCommentException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceUnitTest {
    @Mock
    private CommentStorageDb commentStorage;
    @Mock
    private BookingStorageDb bookingStorage;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @InjectMocks
    private CommentService commentService;
    private User owner;
    private User requestor;
    private Item item;

    @BeforeEach
    void beforeEach() {
        requestor = new User();
        requestor.setId(25);
        requestor.setName("user");
        requestor.setEmail("user@mail.com");

        owner = new User();
        owner.setId(10);
        owner.setName("owner");
        owner.setEmail("owner@mail.com");

        item = new Item();
        item.setId(10);
        item.setName("item");
        item.setAvailable(true);
        item.setOwner(owner);
    }

    @Test
    void testAddComment() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(new User());
        booking.setStatus(BookingStatus.WAITING);

        Comment comment = new Comment();
        comment.setText("comment");
        comment.setAuthor(owner);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2023, 6, 5, 11, 0));

        when(userService.getUserById(anyInt())).thenReturn(requestor);
        when(itemService.getItemById(anyInt())).thenReturn(item);
        when(bookingStorage.getBookingsByBookerAndItemAndEndIsBeforeAndStatus(any(User.class), any(Item.class), any(LocalDateTime.class), any(BookingStatus.class))
        ).thenReturn(List.of(booking));

        when(commentStorage.save(any(Comment.class))).thenReturn(comment);

        Comment addedComment = commentService.addComment(item.getId(), owner.getId(), comment);

        assertThat(addedComment).usingRecursiveComparison().isEqualTo(comment);
    }

    @Test
    void testAddCommentWithoutFinishedBookings() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(new User());
        booking.setStatus(BookingStatus.WAITING);

        Comment comment = new Comment();
        comment.setText("comment");
        comment.setAuthor(owner);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2023, 6, 5, 11, 0));

        when(userService.getUserById(anyInt())).thenReturn(requestor);
        when(itemService.getItemById(anyInt())).thenReturn(item);
        when(bookingStorage.getBookingsByBookerAndItemAndEndIsBeforeAndStatus(any(User.class), any(Item.class), any(LocalDateTime.class), any(BookingStatus.class))
        ).thenReturn(List.of());

        assertThatThrownBy(() -> commentService.addComment(item.getId(), owner.getId(), comment))
                .isInstanceOf(CannotCommentException.class)
                .hasMessageContaining("пользователь с id 10 не может комментировать товар с id 10");

    }
}
