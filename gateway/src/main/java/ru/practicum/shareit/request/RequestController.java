package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestClient requestClient;
    private static final String HEADER_PARAM = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_PARAM) int userId, @Valid @RequestBody RequestDto requestDto) {
        return requestClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") int userId) {
        return requestClient.getAll(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "2000") int size) {
        return requestClient.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestByIdFull(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int requestId) {
        return requestClient.getRequestByIdFull(userId, requestId);
    }
}
