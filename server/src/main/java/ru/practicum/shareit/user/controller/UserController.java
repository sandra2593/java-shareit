package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto user) {
        return UserMapper.toUserDto(userService.create(UserMapper.fromUserDto(user)));
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable int id, @RequestBody UserDto user) {
        return UserMapper.toUserDto(userService.update(id, UserMapper.fromUserDto(user)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable int id) {
        return UserMapper.toUserDto(userService.getUserById(id));
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
