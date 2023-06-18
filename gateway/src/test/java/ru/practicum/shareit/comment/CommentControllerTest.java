package ru.practicum.shareit.comment;

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
import ru.practicum.shareit.comment.client.CommentClient;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.ExceptionController;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
    @Mock
    private CommentClient commentClient;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mvc;

    private CommentDto commentDto;

    private ResponseEntity<Object> commentResponse;

    private static final String HEADER_PARAM = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {

        mvc = MockMvcBuilders.standaloneSetup(commentController).setControllerAdvice(ExceptionController.class).build();

        commentDto = CommentDto.builder().id(1).text("comment").authorName("user")
                .created(LocalDateTime.of(2023, 6, 6, 11, 0)).build();

        commentResponse = new ResponseEntity<>(TestConvert.asJsonString(commentDto), HttpStatus.OK);
    }

    @Test
    void testAddComment() throws Exception {
        when(commentClient.addComment(anyInt(), anyInt(), any())).thenReturn(commentResponse);

        mvc.perform(post("/items/1/comment").content(TestConvert.asJsonString(commentDto)).header(HEADER_PARAM, 1).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created[0]", is(commentDto.getCreated().getYear())))
                .andExpect(jsonPath("$.created[1]", is(commentDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$.created[2]", is(commentDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$.created[3]", is(commentDto.getCreated().getHour())))
                .andExpect(jsonPath("$.created[4]", is(commentDto.getCreated().getMinute())));
    }
}
