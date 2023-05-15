package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;

@Component
public class MemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int userCounter = 0;

    @Override
    public User create(User user) {
        if (duplicateEmail(user)) {
            throw new DuplicateEmailException("есть такой email "+user.getEmail());
        }
        userCounter++;
        user.setId(userCounter);
        users.put(user.getId(),user);
        return users.get(user.getId());
    }

    @Override
    public User update(User user) {
        if (duplicateEmail(user)) {
            throw new DuplicateEmailException("есть такой email "+user.getEmail());
        }
        users.put(user.getId(),user);
        return users.get(user.getId());
    }

    @Override
    public void delete(int id) {
        users.remove(getUserById(id).getId());
    }

    @Override
    public User getUserById(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("нет пользователя с id "+ id);
        }
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    private Boolean duplicateEmail(User user) {
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail()) && u.getId() != user.getId()) {
                return true;
            }
        }
        return false;
    }
}
