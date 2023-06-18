package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorageDb;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.request.dto.RequestAnswerDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestStorageDb;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class RequestService implements RequestServiceInterface {
    private final RequestStorageDb requestStorage;
    private final ItemStorageDb itemStorage;
    private final UserService userService;


    @Autowired
    public RequestService(RequestStorageDb requestStorage, ItemStorageDb itemStorage, UserService userService) {
        this.requestStorage = requestStorage;
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Request create(int userId, Request newRequest) {
        User requestor = userService.getUserById(userId);

        Request request = new Request();
        request.setRequestor(requestor);
        request.setDescription(newRequest.getDescription());

        return requestStorage.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<RequestAnswerDto> getAll(int userId) {
        User requestor = userService.getUserById(userId);
        Collection<Request> userRequests = requestStorage.findRequestsByRequestor(requestor);
        Collection<Item> requestedItems = itemStorage.findItemsByRequestInAndOwnerIsNot(userRequests, requestor);

        if (requestedItems.size() > 0) {
            return getItemRequestsWithResponses(requestedItems);
        }

        return userRequests.stream().map(request -> RequestMapper.toRequestAnswerDto(request, requestedItems)).collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<RequestAnswerDto> getAllOtherUsersRequests(int userId, int from, int size) {
        if (!Pagination.isValid(from, size)) {
            throw new ValidationException("некорректная пагинация");
        }

        int newFrom = Pagination.adjustFrom(from, size);

        User user = userService.getUserById(userId);
        Collection<Request> otherUsersRequests = requestStorage.findRequestsByRequestorNot(user, PageRequest.of(newFrom, size));
        Collection<Item> otherUsersRequestedItems = itemStorage.findItemsByRequestIn(otherUsersRequests);

        return getItemRequestsWithResponses(otherUsersRequestedItems);
    }

    @Override
    @Transactional(readOnly = true)
    public RequestAnswerDto getRequestByIdFull(int userId, int requestId) {
        User user = userService.getUserById(userId);
        Request request = getItemRequestById(user.getId(), requestId);
        Collection<Item> requestedItems = itemStorage.findItemsByRequestIn(List.of(request));

        return RequestMapper.toRequestAnswerDto(request, requestedItems);
    }

    @Override
    @Transactional(readOnly = true)
    public Request getItemRequestById(int userId, int requestId) {
        return requestStorage.findById(requestId).orElseThrow(() -> new NotFoundException("нет товара с id " + requestId));
    }

    private Collection<RequestAnswerDto> getItemRequestsWithResponses(Collection<Item> requestedItems) {
        Map<Request, List<Item>> requestToItemsMap = requestedItems.stream().collect(groupingBy(Item::getRequest, toList()));

        Collection<RequestAnswerDto> result = new ArrayList<>();
        for (Request key : requestToItemsMap.keySet()) {
            result.add(RequestMapper.toRequestAnswerDto(key, requestToItemsMap.get(key)));
        }
        return result;
    }
}
