package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemServiceInterface {
    Item create(int userId, Item item);

    Item update(int itemId, int userId, Item item);

    Item getItemById(int id);

    Collection<Item> getUsersItems(int userId);

    ItemDto getItemByIdWithBookingIntervals(int itemId, int userId);

    Collection<Item> getUserItems(int userId);

    Collection<ItemDto> getUserItemsWithBookingIntervals(int userId);

    Collection<Item> searchItems(String text);
}
