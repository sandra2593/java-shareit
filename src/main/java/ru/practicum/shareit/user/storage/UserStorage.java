package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User newUser);

    User update(User newUser);

    void delete(int userId);

    User getUserById(int userId);

    Collection<User> getAll();
}
