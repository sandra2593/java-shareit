package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto user) {
        return userClient.create(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable int id, @RequestBody UserDto user) {
        return userClient.update(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id) {
        return userClient.delete(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable int id) {
        return userClient.getUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.getAll();
    }
}
