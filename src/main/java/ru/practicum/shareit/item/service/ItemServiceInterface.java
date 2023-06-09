package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemServiceInterface {
    Item create(int userId, ItemDto newItemDto);

    Item update(int itemId, int userId, Item item);

    Item getItemById(int id);

    ItemDto getItemByIdWithBookingIntervals(int itemId, int userId);

    Collection<Item> getUserItems(int userId);

    Collection<ItemDto> getUserItemsWithBookingIntervals(int userId);

    Collection<Item> searchItems(String text, Pageable pageable);
}
