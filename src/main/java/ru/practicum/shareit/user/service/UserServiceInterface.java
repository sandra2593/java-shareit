package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserServiceInterface {
    User create(User user);

    User update(int id, User user);

    void delete(int id);

    User getUserById(int id);

    Collection<User> getAll();
}
