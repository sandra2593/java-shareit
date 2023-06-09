package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.TestConvert;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ExceptionController;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingClient bookingClient;
    @InjectMocks
    private BookingController bookingController;
    private MockMvc mvc;
    private BookingDtoShort bookingDtoShort;
    private BookingDto bookingDto;
    private ResponseEntity<Object> bookingResponse;
    private static final String HEADER_PARAM = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(bookingController).setControllerAdvice(ExceptionController.class).build();

        bookingDtoShort = BookingDtoShort.builder().itemId(1).start(LocalDateTime.of(2023, 6, 5, 11, 0)).end(LocalDateTime.of(2023, 6, 6, 11, 0)).build();

        ItemDtoShort itemDtoShort = ItemDtoShort.builder().id(10).name("item").build();
        UserDtoShort userDtoShort = UserDtoShort.builder().id(20).name("user").build();

        bookingDto = BookingDto.builder().id(1).start(LocalDateTime.of(2023, 6, 5, 11, 0)).end(LocalDateTime.of(2023, 6, 6, 11, 0))
                .item(itemDtoShort).booker(userDtoShort).status(BookingState.WAITING).build();

        bookingResponse = new ResponseEntity<>(TestConvert.asJsonString(bookingDto), HttpStatus.OK);

    }

    @Test
    void testCreate() throws Exception {
        when(bookingClient.create(anyInt(), any(BookingDtoShort.class))).thenReturn(bookingResponse);

        mvc.perform(post("/bookings").content(TestConvert.asJsonString(bookingDtoShort)).header(HEADER_PARAM, 1).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$.start[4]", is(bookingDto.getStart().getMinute())))
                .andExpect(jsonPath("$.end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$.end[4]", is(bookingDto.getEnd().getMinute())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void testApprove() throws Exception {
        when(bookingClient.approve(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingResponse);

        mvc.perform(patch("/bookings/1?approved=true").content(TestConvert.asJsonString(bookingDtoShort)).header(HEADER_PARAM, 1).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$.start[4]", is(bookingDto.getStart().getMinute())))
                .andExpect(jsonPath("$.end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$.end[4]", is(bookingDto.getEnd().getMinute())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void testGetBookingById() throws Exception {
        when(bookingClient.getBookingById(anyInt(), anyInt())).thenReturn(bookingResponse);

        mvc.perform(get("/bookings/1").header(HEADER_PARAM, 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$.start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$.start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$.start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$.start[4]", is(bookingDto.getStart().getMinute())))
                .andExpect(jsonPath("$.end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$.end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$.end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$.end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$.end[4]", is(bookingDto.getEnd().getMinute())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void testGetBookingByIdNotExist() throws Exception {
        bookingResponse = new ResponseEntity<>(TestConvert.asJsonString(Map.of("message", "нет букинга с id 1")), HttpStatus.NOT_FOUND);
        when(bookingClient.getBookingById(anyInt(), anyInt())).thenReturn(bookingResponse);

        mvc.perform(get("/bookings/1").header(HEADER_PARAM, 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("нет букинга с id 1")));
    }

    @Test
    void testGetUserBookingsWrongStatus() throws Exception {
        mvc.perform(get("/bookings?state=UNSUPPORTED_STATUS").header(HEADER_PARAM, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserBookings() throws Exception {
        bookingResponse = new ResponseEntity<>(TestConvert.asJsonString(List.of(bookingDto)), HttpStatus.OK);
        when(bookingClient.getUserBookings(anyInt(), any(BookingState.class), anyInt(), anyInt())).thenReturn(bookingResponse);

        mvc.perform(get("/bookings").header(HEADER_PARAM, 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$[0].start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$[0].start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$[0].start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$[0].start[4]", is(bookingDto.getStart().getMinute())))
                .andExpect(jsonPath("$[0].end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$[0].end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$[0].end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$[0].end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$[0].end[4]", is(bookingDto.getEnd().getMinute())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));

    }

    @Test
    void testGetUserBookingsNotExist() throws Exception {
        bookingResponse = new ResponseEntity<>(TestConvert.asJsonString(Map.of("message", "нет пользователя с id 1")), HttpStatus.NOT_FOUND);
        when(bookingClient.getUserBookings(anyInt(), any(BookingState.class), anyInt(), anyInt())).thenReturn(bookingResponse);

        mvc.perform(get("/bookings").header(HEADER_PARAM, 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("нет пользователя с id 1")));
    }

    @Test
    void testGetOwnedItemsBookings() throws Exception {
        bookingResponse = new ResponseEntity<>(TestConvert.asJsonString(List.of(bookingDto)), HttpStatus.OK);
        when(bookingClient.getOwnedItemsBookings(anyInt(), any(BookingState.class), anyInt(), anyInt())).thenReturn(bookingResponse);

        mvc.perform(get("/bookings/owner").header(HEADER_PARAM, 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start[0]", is(bookingDto.getStart().getYear())))
                .andExpect(jsonPath("$[0].start[1]", is(bookingDto.getStart().getMonthValue())))
                .andExpect(jsonPath("$[0].start[2]", is(bookingDto.getStart().getDayOfMonth())))
                .andExpect(jsonPath("$[0].start[3]", is(bookingDto.getStart().getHour())))
                .andExpect(jsonPath("$[0].start[4]", is(bookingDto.getStart().getMinute())))
                .andExpect(jsonPath("$[0].end[0]", is(bookingDto.getEnd().getYear())))
                .andExpect(jsonPath("$[0].end[1]", is(bookingDto.getEnd().getMonthValue())))
                .andExpect(jsonPath("$[0].end[2]", is(bookingDto.getEnd().getDayOfMonth())))
                .andExpect(jsonPath("$[0].end[3]", is(bookingDto.getEnd().getHour())))
                .andExpect(jsonPath("$[0].end[4]", is(bookingDto.getEnd().getMinute())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }
}
