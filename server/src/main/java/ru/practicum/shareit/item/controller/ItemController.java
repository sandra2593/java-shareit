package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER_PARAM = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader(value = HEADER_PARAM) int userId, @RequestBody ItemDto newItemDto) {
        return ItemMapper.toItemDto(itemService.create(userId, newItemDto));
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable int id, @RequestHeader(HEADER_PARAM) int userId, @RequestBody ItemDto newItem) {
        return ItemMapper.toItemDto(itemService.update(id, userId, ItemMapper.fromItemDto(newItem)));
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable int id, @RequestHeader(HEADER_PARAM) int userId) {
        return itemService.getItemByIdWithBookingIntervals(id, userId);
    }

    @GetMapping
    public Collection<ItemDto> getUserItems(@RequestHeader(HEADER_PARAM) int userId) {
        return itemService.getUserItemsWithBookingIntervals(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestHeader(HEADER_PARAM) int userId, @RequestParam String text, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "2000") int size) {
        return itemService.searchItems(text, from, size).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
