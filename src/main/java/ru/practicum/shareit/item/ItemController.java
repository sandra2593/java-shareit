package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.pagination.Pagination;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
    public ItemDto create(@RequestHeader(value = HEADER_PARAM) int userId, @Valid @RequestBody ItemDto newItemDto) {
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
    Collection<ItemDto> getUserItems(@RequestHeader(HEADER_PARAM) int userId) {
        return itemService.getUserItemsWithBookingIntervals(userId);
    }

    @GetMapping("/search")
    Collection<ItemDto> searchItems(@RequestHeader(HEADER_PARAM) int userId, @NotBlank @RequestParam String text, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "2000") int size) {
        if (!Pagination.isValid(from, size)) {
            throw new ValidationException("некорректная пагинация");
        }

        int newFrom = Pagination.adjustFrom(from, size);

        return itemService.searchItems(text, PageRequest.of(newFrom, size)).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
