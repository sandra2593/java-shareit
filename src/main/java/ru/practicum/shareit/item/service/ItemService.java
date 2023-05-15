package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemService(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public Item create(int userId, Item item) {
        User owner = userStorage.getUserById(userId);
        item.setOwner(owner);
        return itemStorage.create(item);
    }

    public Item update(int itemId, int userId, Item item) {
        Item itemForUpd = itemStorage.getItemById(itemId);
        if (itemForUpd.getOwner().getId() !=  userStorage.getUserById(userId).getId()) {
            throw new NotFoundException("владельцем товара является не " + userStorage.getUserById(userId).getId());
        }
        Item newItem = Item.builder()
                .id(itemForUpd.getId())
                .name(itemForUpd.getName())
                .description(itemForUpd.getDescription())
                .available(itemForUpd.getAvailable())
                .owner(itemForUpd.getOwner())
                .request(itemForUpd.getRequest())
                .build();

        if (item.getName() != null) {
            newItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }
        return itemStorage.update(newItem);
    }

    public Item getItemById(int id) {
        return itemStorage.getItemById(id);
    }

    public Collection<Item> getUsersItems(int userId) {
        User user = userStorage.getUserById(userId);

        return itemStorage.getUsersItems(user.getId());
    }

    public Collection<Item> searchItems(String text) {
        return itemStorage.searchItems(text);
    }
}
