package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(int id, User user) {
        User userForUpd = userStorage.getUserById(id);
        User newUser = User.builder()
                .id(userForUpd.getId())
                .name(userForUpd.getName())
                .email(userForUpd.getEmail())
                .build();

        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        return userStorage.update(newUser);
    }

    public void delete(int id) {
        userStorage.delete(id);
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }
}
