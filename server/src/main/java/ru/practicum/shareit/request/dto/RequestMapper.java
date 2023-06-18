package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.util.Collection;
import java.util.stream.Collectors;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(
                        UserDtoShort.builder()
                                .id(request.getRequestor().getId())
                                .name(request.getRequestor().getName())
                                .build()
                )
                .created(request.getCreated())
                .build();
    }

    public static RequestAnswerDto toRequestAnswerDto(Request itemRequest, Collection<Item> items) {
        Collection<ItemRequestDto> itemDtos = items.stream()
                .map(ItemMapper::toItemRequestDto)
                .collect(Collectors.toList());

        return RequestAnswerDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemDtos)
                .build();
    }

    public static Request fromRequestDto(RequestDto itemRequestDto) {
        Request itemRequest = new Request();
        itemRequest.setDescription(itemRequestDto.getDescription());

        return itemRequest;
    }
}
