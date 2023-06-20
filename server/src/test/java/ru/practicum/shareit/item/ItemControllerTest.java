//package ru.practicum.shareit.item;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import ru.practicum.shareit.TestConvert;
//import ru.practicum.shareit.booking.dto.BookingTimeIntervalDto;
//import ru.practicum.shareit.comment.dto.CommentDto;
//import ru.practicum.shareit.exception.NotFoundException;
//import ru.practicum.shareit.exception.controller.ExceptionController;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.service.ItemService;
//import ru.practicum.shareit.request.model.Request;
//import ru.practicum.shareit.user.model.User;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.hamcrest.Matchers.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//@ExtendWith(MockitoExtension.class)
//public class ItemControllerTest {
//    @Mock
//    private ItemService itemService;
//
//    @InjectMocks
//    private ItemController itemController;
//
//    private MockMvc mvc;
//
//    private ItemDto itemDto;
//
//    private ItemDto itemDtoWithNulls;
//
//    private Item item;
//
//    private CommentDto commentDto;
//
//    private static final String HEADER_PARAM = "X-Sharer-User-Id";
//
//    @BeforeEach
//    void beforeEach() {
//        BookingTimeIntervalDto bookingTimeIntervalDtoLast = BookingTimeIntervalDto.builder().id(1).start(LocalDateTime.of(2023, 5, 5, 11, 0))
//                .end(LocalDateTime.of(2023, 5, 6, 11, 0)).bookerId(1).build();
//        BookingTimeIntervalDto bookingTimeIntervalDtoNext = BookingTimeIntervalDto.builder().id(1).start(LocalDateTime.of(2023, 6, 5, 11, 0))
//                .end(LocalDateTime.of(2023, 6, 6, 11, 0)).bookerId(1).build();
//
//        User user = new User();
//        user.setId(1);
//        user.setName("user");
//        user.setEmail("user@mail.com");
//
//        Request request = new Request();
//        request.setId(1);
//        request.setDescription("description");
//        request.setRequestor(user);
//        request.setCreated(LocalDateTime.of(2023, 5, 5, 11, 0));
//
//        mvc = MockMvcBuilders.standaloneSetup(itemController).setControllerAdvice(ExceptionController.class).build();
//
//        commentDto = CommentDto.builder().id(1).text("comment").authorName("user")
//                .created(LocalDateTime.of(2023, 5, 6, 11, 0)).build();
//
//        itemDto = ItemDto.builder().id(1).name("item").description("description")
//                .available(true).lastBooking(bookingTimeIntervalDtoLast)
//                .nextBooking(bookingTimeIntervalDtoNext).comments(List.of(commentDto))
//                .requestId(1).build();
//
//        itemDtoWithNulls = ItemDto.builder().id(1).name("item").description("description")
//                .available(true).lastBooking(null).nextBooking(null).comments(null).requestId(1).build();
//
//        item = new Item();
//        item.setId(1);
//        item.setName("item");
//        item.setDescription("description");
//        item.setAvailable(true);
//        item.setOwner(user);
//        item.setRequest(request);
//
//    }
//
//    @Test
//    void testCreate() throws Exception {
//        when(itemService.create(anyInt(), any())).thenReturn(item);
//
//        mvc.perform(post("/items")
//                        .content(TestConvert.asJsonString(itemDto))
//                        .header(HEADER_PARAM, 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
//                .andExpect(jsonPath("$.name", is(itemDto.getName())))
//                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    void testCreateMissingUserIdHeader() throws Exception {
//        mvc.perform(post("/items").content(TestConvert.asJsonString(itemDto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//
//    }
//
//    @Test
//    void testUpdate() throws Exception {
//        when(itemService.update(anyInt(), anyInt(), any())).thenReturn(item);
//
//        mvc.perform(patch("/items/1").content(TestConvert.asJsonString(itemDto)).header(HEADER_PARAM, 1).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
//                .andExpect(jsonPath("$.name", is(itemDto.getName())))
//                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    void testUpdateUserDoesntExist() throws Exception {
//        when(itemService.update(anyInt(), anyInt(), any())).thenThrow(new NotFoundException("нет товара с id 111"));
//
//        mvc.perform(patch("/items/111").content(TestConvert.asJsonString(itemDto)).header(HEADER_PARAM, 111).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andExpect(content().string(containsString("message")))
//                .andExpect(jsonPath("$.message", is("нет товара с id 111")));
//    }
//
//    @Test
//    void testGetItemById() throws Exception {
//        when(itemService.getItemByIdWithBookingIntervals(anyInt(), anyInt())).thenReturn(itemDto);
//
//        mvc.perform(get("/items/1").content(TestConvert.asJsonString(itemDto)).header(HEADER_PARAM, 1L).accept(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
//                .andExpect(jsonPath("$.name", is(itemDto.getName())))
//                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
//    }
//
//    @Test
//    void testGetItemByIdNotExist() throws Exception {
//        when(itemService.getItemByIdWithBookingIntervals(anyInt(), anyInt())).thenThrow(new NotFoundException("нет товара с id " + 1));
//
//        mvc.perform(get("/items/1").content(TestConvert.asJsonString(itemDto)).header(HEADER_PARAM, 1).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string(containsString("message")))
//                .andExpect(jsonPath("$.message", is("нет товара с id 1")));
//    }
//
//    @Test
//    void testGetUserItems() throws Exception {
//        when(itemService.getUserItemsWithBookingIntervals(anyInt())).thenReturn(List.of(itemDtoWithNulls));
//
//        mvc.perform(get("/items?from=1&size=20").content(TestConvert.asJsonString(itemDto)).header(HEADER_PARAM, 1).accept(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].id", is(itemDtoWithNulls.getId()), Integer.class))
//                .andExpect(jsonPath("$[0].name", is(itemDtoWithNulls.getName())))
//                .andExpect(jsonPath("$[0].description", is(itemDtoWithNulls.getDescription())))
//                .andExpect(jsonPath("$[0].available", is(itemDtoWithNulls.getAvailable())))
//                .andExpect(jsonPath("$[0].requestId", is(itemDtoWithNulls.getRequestId()), Integer.class));
//    }
//
//    @Test
//    void testSearchItems() throws Exception {
//        when(itemService.searchItems(anyString(), anyInt(), anyInt())).thenReturn(List.of(item));
//
//        mvc.perform(get("/items/search?text=search&from=1&size=20").header(HEADER_PARAM, 1).accept(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].id", is(itemDtoWithNulls.getId()), Integer.class))
//                .andExpect(jsonPath("$[0].name", is(itemDtoWithNulls.getName())))
//                .andExpect(jsonPath("$[0].description", is(itemDtoWithNulls.getDescription())))
//                .andExpect(jsonPath("$[0].available", is(itemDtoWithNulls.getAvailable())))
//                .andExpect(jsonPath("$[0].requestId", is(itemDtoWithNulls.getRequestId()), Integer.class));
//    }
//
//    @Test
//    void testSearchItemsEmptyTextParam() throws Exception {
//        mvc.perform(get("/items/search?&from=1&size=20").header(HEADER_PARAM, 1).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//}
