package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorageDb;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentStorageDb;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorageDb;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemStorageDb itemStorage;
    private final CommentStorageDb commentStorage;
    private final UserService userService;
    private final BookingStorageDb bookingStorage;

    @Autowired
    public ItemService(ItemStorageDb itemStorage, UserService userService, CommentStorageDb commentStorage, BookingStorageDb bookingStorage) {
        this.itemStorage = itemStorage;
        this.userService = userService;
        this.commentStorage = commentStorage;
        this.bookingStorage = bookingStorage;
    }

    @Transactional
    public Item create(int userId, Item item) {
        User owner = userService.getUserById(userId);
        item.setOwner(owner);

        return itemStorage.save(item);
    }

    @Transactional
    public Item update(int itemId, int userId, Item item) {
        Item itemToUpdate = getItemById(itemId);
        User user = userService.getUserById(userId);

        if (itemToUpdate.getOwner().getId() != user.getId()) {
            throw new NotOwnerException("пользователя с id " + userId + " не владелец товара с id " + itemId);
        }
        Item itemForUpd = new Item();

        itemForUpd.setId(itemToUpdate.getId());
        itemForUpd.setName(itemToUpdate.getName());
        itemForUpd.setDescription(itemToUpdate.getDescription());
        itemForUpd.setAvailable(itemToUpdate.getAvailable());
        itemForUpd.setOwner(itemToUpdate.getOwner());
        itemForUpd.setRequest(itemToUpdate.getRequest());

        if (Objects.nonNull(item.getName())) {
            itemForUpd.setName(item.getName());
        }
        if (Objects.nonNull(item.getDescription())) {
            itemForUpd.setDescription(item.getDescription());
        }
        if (Objects.nonNull(item.getAvailable())) {
            itemForUpd.setAvailable(item.getAvailable());
        }

        return itemStorage.save(itemForUpd);
    }

    @Transactional
    public Item getItemById(int id) {
        return itemStorage.findById(id).orElseThrow(() -> new NotFoundException("нет товара с id " + id));
    }

    @Transactional
    public Collection<Item> getUsersItems(int userId) {
        User user = userService.getUserById(userId);

        return itemStorage.findItemByOwner(user);
    }

    @Transactional
    public ItemDto getItemByIdWithBookingIntervals(int itemId, int userId) {
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Collection<Comment> comments = commentStorage.getCommentsByItems(List.of(item));
        itemDto.setComments(comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));

        if (item.getOwner().getId() == user.getId()) {
            List<Booking> lastBookings = bookingStorage.getItemsLastBookings(List.of(item));
            List<Booking> nextBookings = bookingStorage.getItemsNextBookings(List.of(item));

            if (!lastBookings.isEmpty()) {
                itemDto.setLastBooking(BookingMapper.toBookingTimeIntervalDto(lastBookings.get(0)));
            }
            if (!nextBookings.isEmpty()) {
                itemDto.setNextBooking(BookingMapper.toBookingTimeIntervalDto(nextBookings.get(0)));
            }
        }

        return itemDto;
    }

    @Transactional(readOnly = true)
    public Collection<Item> getUserItems(int userId) {
        User user = userService.getUserById(userId);
        return itemStorage.findItemByOwner(user);
    }

    @Transactional(readOnly = true)
    public Collection<ItemDto> getUserItemsWithBookingIntervals(int userId) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        List<ItemDto> itemDtoListNullIntervals = new ArrayList<>();
        Map<Item, Collection<Comment>> itemToComments = new HashMap<>();

        User user = userService.getUserById(userId);
        Collection<Item> items = getUserItems(user.getId());

        Map<Item, Booking> lastBookings = bookingStorage.getItemsLastBookings(items).stream().collect(Collectors.toMap(Booking::getItem, val -> val));
        Map<Item, Booking> nextBookings = bookingStorage.getItemsNextBookings(items).stream().collect(Collectors.toMap(Booking::getItem, val -> val));
        Collection<Comment> comments = commentStorage.getCommentsByItems(items);

        for (Comment comment : comments) {
            if (itemToComments.containsKey(comment.getItem())) {
                itemToComments.get(comment.getItem()).add(comment);
            } else {
                itemToComments.put(comment.getItem(), List.of(comment));
            }
        }

        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            Booking lastBooking = lastBookings.get(item);
            Booking nextBooking = nextBookings.get(item);
            Collection<Comment> itemComments = itemToComments.get(item);

            if (lastBooking == null && nextBooking == null) {
                itemDtoListNullIntervals.add(itemDto);
                continue;
            }

            if (lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toBookingTimeIntervalDto(lastBooking));
            }
            if (nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toBookingTimeIntervalDto(nextBooking));
            }
            if (itemComments != null) {
                itemDto.setComments(itemComments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));
            }
            itemDtoList.add(itemDto);
        }

        itemDtoList.addAll(itemDtoListNullIntervals);

        return itemDtoList;
    }

    @Transactional
    public Collection<Item> searchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }

        return itemStorage.searchItems(text.toLowerCase());
    }
}
