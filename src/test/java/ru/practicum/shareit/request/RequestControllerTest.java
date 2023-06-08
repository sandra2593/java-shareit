package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.TestConvert;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.controller.ExceptionController;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestAnswerDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {
    @Mock
    private RequestService requestService;

    @InjectMocks
    private RequestController requestController;

    private MockMvc mvc;

    private Request request;

    private RequestDto requestDto;

    private RequestAnswerDto requestAnswerDto;

    private ItemRequestDto itemRequestDto;

    private UserDtoShort userDtoShort;

    private static final String HEADER_PARAM = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {
        User user = new User();
        user.setId(1);
        user.setName("user");
        user.setEmail("user@mail.com");

        mvc = MockMvcBuilders.standaloneSetup(requestController).setControllerAdvice(ExceptionController.class).build();

        userDtoShort = UserDtoShort.builder().id(1).name("user").build();

        request = new Request();
        request.setId(1);
        request.setDescription("description");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.of(2023, 6, 5, 11, 0));

        requestDto = RequestDto.builder().id(1).description("description").requestor(userDtoShort)
                .created(LocalDateTime.of(2023, 6, 5, 11, 0)).build();

        itemRequestDto = ItemRequestDto.builder().id(1)
                .name("name").description("description").available(true).requestId(1).build();

        requestAnswerDto = RequestAnswerDto.builder().id(1).description("description").created(LocalDateTime.of(2023, 6, 5, 11, 0)).items(List.of(itemRequestDto)).build();
    }

    @Test
    void testCreate() throws Exception {
        when(requestService.create(anyInt(), any(Request.class))).thenReturn(request);

        mvc.perform(post("/requests").content(TestConvert.asJsonString(requestDto)).header(HEADER_PARAM, 1).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(userDtoShort.getId()), Integer.class))
                .andExpect(jsonPath("$.requestor.name", is(userDtoShort.getName())))
                .andExpect(jsonPath("$.created[0]", is(requestDto.getCreated().getYear())))
                .andExpect(jsonPath("$.created[1]", is(requestDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$.created[2]", is(requestDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$.created[3]", is(requestDto.getCreated().getHour())))
                .andExpect(jsonPath("$.created[4]", is(requestDto.getCreated().getMinute())));

    }

    @Test
    void testCreateMissingUserIdHeader() throws Exception {
        mvc.perform(post("/requests").content(TestConvert.asJsonString(requestDto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testCreateUserNotFound() throws Exception {
        when(requestService.create(anyInt(), any(Request.class))).thenThrow(new NotFoundException("нет пользователя с id " + 1));

        mvc.perform(post("/requests").content(TestConvert.asJsonString(requestDto)).header(HEADER_PARAM, 1).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("нет пользователя с id 1")));

    }

    @Test
    void testGetAll() throws Exception {
        when(requestService.getAll(anyInt())).thenReturn(List.of(requestAnswerDto));

        mvc.perform(get("/requests").header(HEADER_PARAM, 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestAnswerDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(requestAnswerDto.getDescription())))
                .andExpect(jsonPath("$[0].created[0]", is(requestAnswerDto.getCreated().getYear())))
                .andExpect(jsonPath("$[0].created[1]", is(requestAnswerDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$[0].created[2]", is(requestAnswerDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$[0].created[3]", is(requestAnswerDto.getCreated().getHour())))
                .andExpect(jsonPath("$[0].created[4]", is(requestAnswerDto.getCreated().getMinute())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequestDto.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemRequestDto.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemRequestDto.getRequestId()), Integer.class));
    }

    @Test
    void testGetRequestsForUserOwnedItems() throws Exception {
        when(requestService.getAllOtherUsersRequests(anyInt(), any(Pageable.class))).thenReturn(List.of(requestAnswerDto));

        mvc.perform(get("/requests/all?from=0&size=4").header(HEADER_PARAM, 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestAnswerDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(requestAnswerDto.getDescription())))
                .andExpect(jsonPath("$[0].created[0]", is(requestAnswerDto.getCreated().getYear())))
                .andExpect(jsonPath("$[0].created[1]", is(requestAnswerDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$[0].created[2]", is(requestAnswerDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$[0].created[3]", is(requestAnswerDto.getCreated().getHour())))
                .andExpect(jsonPath("$[0].created[4]", is(requestAnswerDto.getCreated().getMinute())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequestDto.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemRequestDto.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemRequestDto.getRequestId()), Integer.class));
    }

    @Test
    void testGetRequestsForUserOwnedItemsWrongParams() throws Exception {

        mvc.perform(get("/requests/all?from=-1&size=-999").header(HEADER_PARAM, 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("некорректная пагинация")));
    }

    @Test
    void testGetRequestByIdFull() throws Exception {
        when(requestService.getRequestByIdFull(anyInt(), anyInt())).thenReturn(requestAnswerDto);

        mvc.perform(get("/requests/" + 1).header(HEADER_PARAM, 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(requestAnswerDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(requestAnswerDto.getDescription())))
                .andExpect(jsonPath("$.created[0]", is(requestAnswerDto.getCreated().getYear())))
                .andExpect(jsonPath("$.created[1]", is(requestAnswerDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$.created[2]", is(requestAnswerDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$.created[3]", is(requestAnswerDto.getCreated().getHour())))
                .andExpect(jsonPath("$.created[4]", is(requestAnswerDto.getCreated().getMinute())))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.items[0].name", is(itemRequestDto.getName())))
                .andExpect(jsonPath("$.items[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemRequestDto.getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(itemRequestDto.getRequestId()), Integer.class));
    }
}
