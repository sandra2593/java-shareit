package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String HEADER_PARAM = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(value = HEADER_PARAM) int userId, @Valid @RequestBody ItemDto newItemDto) {
        return itemClient.create(userId, newItemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable int id, @RequestHeader(HEADER_PARAM) int userId, @RequestBody ItemDto newItem) {
        return itemClient.update(id, userId, newItem);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable int id, @RequestHeader(HEADER_PARAM) int userId) {
        return itemClient.getItemById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(HEADER_PARAM) int userId) {
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(HEADER_PARAM) int userId, @RequestParam String text, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "2000") int size) {
        return itemClient.searchItems(userId, text, from, size);
    }
}
