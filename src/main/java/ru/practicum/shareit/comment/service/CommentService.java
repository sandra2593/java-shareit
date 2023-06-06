package ru.practicum.shareit.comment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.storage.BookingStorageDb;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentStorageDb;
import ru.practicum.shareit.exception.CannotCommentException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class CommentService implements CommentServiceInterface {
    private final UserService userService;
    private final CommentStorageDb commentStorage;
    private final ItemService itemService;
    private final BookingStorageDb bookingStorage;

    @Autowired
    public CommentService(UserService userService, CommentStorageDb commentStorage, ItemService itemService, BookingStorageDb bookingStorage) {
        this.itemService = itemService;
        this.commentStorage = commentStorage;
        this.userService = userService;
        this.bookingStorage = bookingStorage;
    }

    @Override
    @Transactional
    public Comment addComment(int itemId, int userId, Comment newComment) {
        User user = userService.getUserById(userId);
        Item item = itemService.getItemById(itemId);
        Collection<Booking> finishedBookings = bookingStorage.getBookingsByBookerAndItemAndEndIsBeforeAndStatus(user, item, LocalDateTime.now(), BookingState.APPROVED);

        if (finishedBookings.size() == 0) {
            throw new CannotCommentException("пользователь с id " + userId + "не может комментировать товар с id " + itemId + " ");
        }

        newComment.setItem(item);
        newComment.setAuthor(user);
        newComment.setCreated(LocalDateTime.now());

        return commentStorage.save(newComment);
    }
}
