package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.request.dto.RequestAnswerDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;
    private static final String HEADER_PARAM = "X-Sharer-User-Id";

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    RequestDto create(@RequestHeader(HEADER_PARAM) int userId, @Valid @RequestBody RequestDto requestDto) {
        return RequestMapper.toRequestDto(requestService.create(userId, RequestMapper.fromRequestDto(requestDto)));
    }

    @GetMapping
    Collection<RequestAnswerDto> getAll(@RequestHeader("X-Sharer-User-Id") int userId) {
        return requestService.getAll(userId);
    }

    @GetMapping("/all")
    Collection<RequestAnswerDto> getOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "2000") int size) {
        if (!Pagination.isValid(from, size)) {
            throw new ValidationException("некорректная пагинация");
        }

        int newFrom = Pagination.adjustFrom(from, size);

        return requestService.getAllOtherUsersRequests(userId, PageRequest.of(newFrom, size));
    }

    @GetMapping("/{requestId}")
    RequestAnswerDto getRequestByIdFull(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int requestId) {
        return  requestService.getRequestByIdFull(userId, requestId);
    }
}
