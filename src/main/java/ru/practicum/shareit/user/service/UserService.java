package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorageDb;

import java.util.Collection;
import java.util.Objects;

@Service
public class UserService implements UserServiceInterface {
    private final UserStorageDb userStorage;

    @Autowired
    public UserService(UserStorageDb userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    @Transactional
    public User create(User user) {
        Boolean isDuplicate = duplicateEmail(user.getId(), user);
        User u = userStorage.save(user);
        if (isDuplicate) {
            userStorage.deleteById(u.getId());
            throw new DuplicateEmailException("есть такой email " + user.getEmail());
        }
        return u;
    }

    @Override
    @Transactional
    public User update(int id, User user) {
        if (duplicateEmail(id, user)) {
            throw new DuplicateEmailException("есть такой email " + user.getEmail());
        }
        User userToUpdate = userStorage.findById(id).orElseThrow(() -> new NotFoundException("нет пользователя с id " + id));
        User newUser = new User();
        newUser.setId(userToUpdate.getId());
        newUser.setName(userToUpdate.getName());
        newUser.setEmail(userToUpdate.getEmail());

        if (Objects.nonNull(user.getEmail())) {
            newUser.setEmail(user.getEmail());
        }
        if (Objects.nonNull(user.getName())) {
            newUser.setName(user.getName());
        }

        return userStorage.save(newUser);
    }

    @Override
    @Transactional
    public void delete(int id) {
        userStorage.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(int id) {
        return userStorage.findById(id).orElseThrow(() -> new NotFoundException("нет пользователя с id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> getAll() {
        return userStorage.findAll();
    }

    private Boolean duplicateEmail(int id, User user) {
        for (User u : userStorage.findAll()) {
            if (u.getEmail().equals(user.getEmail()) && u.getId() != id) {
                return true;
            }
        }
        return false;
    }
}
