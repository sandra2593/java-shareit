package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.RequestAnswerDto;
import ru.practicum.shareit.request.model.Request;

import java.util.Collection;

public interface RequestServiceInterface {
    Request create(int userId, Request newRequest);

    Collection<RequestAnswerDto> getAll(int userId);

    Collection<RequestAnswerDto> getAllOtherUsersRequests(int userId, Pageable pageable);

    RequestAnswerDto getRequestByIdFull(int userId, int requestId);

    Request getItemRequestById(int userId, int requestId);
}
