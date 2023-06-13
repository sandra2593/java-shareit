package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestAnswerDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestServiceIntegrationTest {
    private final EntityManager em;

    private final RequestService requestService;

    private Item item;

    private Request request;


    @BeforeEach
    void beforeEach() {
        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.com");

        User user = new User();
        user.setName("user");
        user.setEmail("user@mail.com");

        request = new Request();
        request.setDescription("Нужна дрель с аккумулятором");
        request.setRequestor(user);

        item = new Item();
        item.setName("дрель не дрель");
        item.setDescription("С Аккумулятором");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);


        em.persist(owner);
        em.persist(user);
        em.persist(request);
        em.persist(item);
        em.flush();
    }

    @Test
    void testGetAll() {
        RequestAnswerDto correctRequest = RequestMapper.toRequestAnswerDto(request, List.of(item));

        ArrayList<RequestAnswerDto> result = new ArrayList<>(requestService.getAll(2));
        assertThat(result).hasSize(1);

        RequestAnswerDto resultDto = result.get(0);
        assertThat(resultDto.getId()).isEqualTo(correctRequest.getId());
        assertThat(resultDto.getDescription()).isEqualTo(correctRequest.getDescription());

        ArrayList<ItemRequestDto> items = new ArrayList<>(resultDto.getItems());
        assertThat(resultDto.getItems()).hasSize(1);

        ItemRequestDto requestedItem = items.get(0);
        ItemRequestDto correctRequestedItem = new ArrayList<>(correctRequest.getItems()).get(0);
        assertThat(requestedItem.getId()).isEqualTo(correctRequestedItem.getId());
        assertThat(requestedItem.getName()).isEqualTo(correctRequestedItem.getName());
        assertThat(requestedItem.getDescription()).isEqualTo(correctRequestedItem.getDescription());
        assertThat(requestedItem.getAvailable()).isEqualTo(correctRequestedItem.getAvailable());
        assertThat(requestedItem.getRequestId()).isEqualTo(correctRequestedItem.getRequestId());
    }

    @Test
    void testGetAllNotExist() {
        assertThatThrownBy(() -> requestService.getAll(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("нет пользователя с id 999");

    }
}
