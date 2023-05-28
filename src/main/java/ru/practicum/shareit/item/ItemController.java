package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
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
    public ItemDto createItem(@RequestHeader(value = HEADER_PARAM) int userId, @Valid @RequestBody ItemDto newItemDto) {
        return ItemMapper.toItemDto(itemService.create(userId, ItemMapper.fromItemDto(newItemDto)));
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable int id, @RequestHeader(HEADER_PARAM) int userId, @RequestBody ItemDto newItem) {
        return ItemMapper.toItemDto(itemService.update(id, userId, ItemMapper.fromItemDto(newItem)));
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable int id) {
        return ItemMapper.toItemDto(itemService.getItemById(id));
    }

    @GetMapping
    Collection<ItemDto> getUserItems(@RequestHeader(HEADER_PARAM) int userId) {
        return itemService.getUsersItems(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    Collection<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
