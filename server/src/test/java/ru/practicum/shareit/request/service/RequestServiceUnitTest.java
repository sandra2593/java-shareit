package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorageDb;
import ru.practicum.shareit.request.dto.RequestAnswerDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestStorageDb;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceUnitTest {

    @Mock
    private RequestStorageDb requestStorage;

    @Mock
    private ItemStorageDb itemStorage;

    @Mock
    private UserService userService;

    @InjectMocks
    private RequestService requestService;

    private User owner;

    private User requestor;

    private Item item;

    private Request request;

    @BeforeEach
    void beforeEach() {
        owner = new User();
        owner.setId(10);
        owner.setName("owner");
        owner.setEmail("owner@mail.com");

        requestor = new User();
        requestor.setId(25);
        requestor.setName("user");
        requestor.setEmail("user@mail.com");

        request = new Request();
        request.setId(1);
        request.setDescription("description");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.of(2023, 6, 5, 11, 0));

        item = new Item();
        item.setId(10);
        item.setName("name");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);
    }

    @Test
    void testCreate() {
        Request newRequest = new Request();
        newRequest.setDescription("description");

        when(userService.getUserById(anyInt())).thenReturn(requestor);
        when(requestStorage.save(any(Request.class))).thenReturn(request);

        Request createdItemRequest = requestService.create(requestor.getId(), newRequest);

        assertThat(createdItemRequest.getId()).isEqualTo(request.getId());
        assertThat(createdItemRequest.getDescription()).isEqualTo(request.getDescription());
        assertThat(createdItemRequest.getRequestor()).isEqualTo(request.getRequestor());
    }

    @Test
    void testGetAllOtherUsersRequests() {
        when(userService.getUserById(anyInt())).thenReturn(owner);
        when(requestStorage.findRequestsByRequestorNot(any(User.class), any(Pageable.class))).thenReturn(List.of(request));
        when(itemStorage.findItemsByRequestIn(anyCollection())).thenReturn(List.of(item));

        Collection<RequestAnswerDto> resultRequests = requestService.getAllOtherUsersRequests(owner.getId(), 0, 2000);

        assertThat(resultRequests).contains(RequestMapper.toRequestAnswerDto(request, List.of(item)));
    }

    @Test
    void testGetAllOtherUsersRequestsWrongParams() {
        assertThatThrownBy(() -> requestService.getAllOtherUsersRequests(owner.getId(), -1, -999))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("некорректная пагинация");

    }

    @Test
    void testGetRequestByIdFull() {
        when(userService.getUserById(anyInt())).thenReturn(requestor);
        when(requestStorage.findById(anyInt())).thenReturn(Optional.ofNullable(request));
        when(itemStorage.findItemsByRequestIn(anyCollection())).thenReturn(List.of(item));

        RequestAnswerDto foundItemRequest = requestService.getRequestByIdFull(requestor.getId(), request.getId());

        assertThat(foundItemRequest).isEqualTo(RequestMapper.toRequestAnswerDto(request, List.of(item)));
    }
}
