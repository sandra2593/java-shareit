package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemoryItemStorage implements ItemStorage {
    private final HashMap<Integer, Item> items = new HashMap<>();
    private int itemCounter = 0;

    @Override
    public Item create(Item item){
        itemCounter++;
        item.setId(itemCounter);
        items.put(item.getId(),item);
        return items.get(item.getId());
    }

    @Override
    public Item update(Item item){
        items.put(item.getId(),item);
        return items.get(item.getId());
    }

    @Override
    public Item getItemById(int id){
        return items.get(id);
    }

    @Override
    public Collection<Item> getUsersItems(int userId){
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> searchItems(String query){
        if (query.isBlank()) {
            return List.of();
        }

        String newQuery = query.toLowerCase();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(newQuery) ||
                                item.getDescription().toLowerCase().contains(newQuery)
                ).collect(Collectors.toList());
    }
}
