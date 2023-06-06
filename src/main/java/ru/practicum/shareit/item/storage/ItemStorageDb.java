package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

public interface ItemStorageDb extends JpaRepository<Item, Integer> {
    Collection<Item> findItemByOwner(User user);

    @Query(value =
            "SELECT it.id, it.name, it.description, it.is_available, it.owner_id, it.request_id " +
                    "FROM items it " +
                    "WHERE it.is_available IS TRUE AND (LOWER(it.name) LIKE '%' || ?1 || '%' OR LOWER(it.description) LIKE '%' || ?1 || '%')",
            nativeQuery = true)
    List<Item> searchItems(String query, Pageable pageable);

    Collection<Item> findItemsByRequestIn(Collection<Request> itemRequests);

    Collection<Item> findItemsByRequestInAndOwnerIsNot(Collection<Request> itemRequests, User requestor);

}
