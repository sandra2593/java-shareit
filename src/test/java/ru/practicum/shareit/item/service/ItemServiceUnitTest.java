package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentStorageDb;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorageDb;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
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
public class ItemServiceUnitTest {
    @Mock
    private ItemStorageDb itemStorage;
    @Mock
    private CommentStorageDb commentStorage;
    @Mock
    private UserService userService;
    @Mock
    private RequestService requestService;
    @InjectMocks
    private ItemService itemService;
    private User owner;
    private User requestor;
    private Item item;
    private Comment comment;

    private static Item copyItem(Item originalItem) {
        Item copiedItem = new Item();
        copiedItem.setId(originalItem.getId());
        copiedItem.setName(originalItem.getName());
        copiedItem.setDescription(originalItem.getDescription());
        copiedItem.setAvailable(originalItem.getAvailable());
        copiedItem.setOwner(originalItem.getOwner());
        copiedItem.setRequest(originalItem.getRequest());

        return copiedItem;
    }

    @BeforeEach
    void beforeEach() {
        owner = new User();
        owner.setId(10);
        owner.setName("owner");
        owner.setEmail("owner@mail.com");

        item = new Item();
        item.setId(10);
        item.setName("name");
        item.setAvailable(true);
        item.setOwner(owner);

        requestor = new User();
        requestor.setId(25);
        requestor.setName("user");
        requestor.setEmail("user@mail.com");

        comment = new Comment();
        comment.setText("comment");
        comment.setAuthor(owner);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2023, 6, 5, 11, 0));
    }

    @Test
    void testCreate() {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(userService.getUserById(anyInt())).thenReturn(owner);
        when(itemStorage.save(any(Item.class))).thenReturn(item);

        Item createdItem = itemService.create(owner.getId(), itemDto);

        assertThat(createdItem).isNotNull();
        assertThat(createdItem).usingRecursiveComparison().isEqualTo(item);

    }

    @Test
    void testCreateWithRequest() {
        User requestor = new User();
        requestor.setId(25);
        requestor.setName("user");
        requestor.setEmail("user@mail.com");

        Request itemRequest = new Request();
        itemRequest.setDescription("Нужна дрель с аккумулятором");
        itemRequest.setRequestor(requestor);

        Item itemWithRequest = copyItem(item);
        itemWithRequest.setRequest(itemRequest);

        ItemDto itemDto = ItemMapper.toItemDto(itemWithRequest);

        when(userService.getUserById(anyInt())).thenReturn(owner);
        when(requestService.getItemRequestById(anyInt(), anyInt())).thenReturn(itemRequest);
        when(itemStorage.save(any(Item.class))).thenReturn(itemWithRequest);

        Item createdItem = itemService.create(owner.getId(), itemDto);

        assertThat(createdItem).usingRecursiveComparison().isEqualTo(itemWithRequest);
    }

    @Test
    void testUpdate() {
        Item newItem = copyItem(item);
        newItem.setDescription("newDescription");

        when(itemStorage.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(anyInt())).thenReturn(owner);
        when(itemStorage.save(any(Item.class))).thenReturn(newItem);

        Item updatedItem = itemService.update(owner.getId(), item.getId(), newItem);

        assertThat(updatedItem).isEqualTo(newItem);
    }

    @Test
    void testUpdateNotOwner() {
        Item newItem = copyItem(item);
        newItem.setDescription("newDescription");

        when(itemStorage.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(anyInt())).thenReturn(requestor);

        assertThatThrownBy(() -> itemService.update(item.getId(), requestor.getId(), newItem))
                .isInstanceOf(NotOwnerException.class)
                .hasMessageContaining("пользователь с id 25 не владелец товара с id 10");
    }

    @Test
    void testGetItemByIdWithBookingIntervals() {
        Booking lastBooking = new Booking();
        lastBooking.setStart(LocalDateTime.now().minusDays(7));
        lastBooking.setEnd(LocalDateTime.now().minusDays(2));
        lastBooking.setItem(item);
        lastBooking.setBooker(requestor);
        lastBooking.setStatus(BookingState.WAITING);

        Booking nextBooking = new Booking();
        nextBooking.setStart(LocalDateTime.now().plusDays(2));
        nextBooking.setEnd(LocalDateTime.now().plusDays(7));
        nextBooking.setItem(item);
        nextBooking.setBooker(requestor);
        nextBooking.setStatus(BookingState.WAITING);

        ItemDto expectedItemDto = ItemMapper.toItemDto(item);
        expectedItemDto.setComments(List.of(CommentMapper.toCommentDto(comment)));

        when(itemStorage.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(anyInt())).thenReturn(requestor);
        when(commentStorage.getCommentsByItems(anyCollection())).thenReturn(List.of(comment));

        ItemDto resultItemDto = itemService.getItemByIdWithBookingIntervals(requestor.getId(), item.getId());

        assertThat(resultItemDto).isEqualTo(expectedItemDto);
    }

    @Test
    void testSearchItems() {
        when(itemStorage.searchItems(anyString(), any(Pageable.class))).thenReturn(List.of(item));

        Collection<Item> foundItems = itemService.searchItems("item", PageRequest.of(0, 2000));

        assertThat(foundItems).contains(item);
    }

    @Test
    void testSearchItemsEmptyText() {
        Collection<Item> foundItems = itemService.searchItems("", PageRequest.of(0, 2000));

        assertThat(foundItems).isEmpty();
    }
}
