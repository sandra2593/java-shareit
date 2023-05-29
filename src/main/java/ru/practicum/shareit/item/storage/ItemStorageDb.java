package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface ItemStorageDb extends JpaRepository<Item, Integer> {
    Collection<Item> findItemByOwner(User user);

    @Query(value =
            "SELECT new Item (it.id, it.name, it.description, it.available, it.owner, it.request) " +
                    "FROM Item it " +
                    "WHERE it.available is TRUE AND (lower(it.name) LIKE '%' || ?1 || '%' OR lower(it.description) LIKE '%' || ?1 || '%')")
    Collection<Item> searchItems(String query);
}