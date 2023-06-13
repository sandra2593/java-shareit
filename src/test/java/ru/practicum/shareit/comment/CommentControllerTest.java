package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.TestConvert;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.controller.ExceptionController;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

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
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mvc;

    private Item item;

    private CommentDto commentDto;

    private Comment comment;

    private static final String HEADER_PARAM = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {

        mvc = MockMvcBuilders.standaloneSetup(commentController).setControllerAdvice(ExceptionController.class).build();

        commentDto = CommentDto.builder().id(1).text("comment").authorName("user")
                .created(LocalDateTime.of(2023, 6, 6, 11, 0)).build();

        User user = new User();
        user.setId(1);
        user.setName("user");
        user.setEmail("user@mail.com");

        Request request = new Request();
        request.setId(1);
        request.setDescription("description");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.of(2023, 6, 5, 11, 0));

        item = new Item();
        item.setId(1);
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(request);

        comment = new Comment();
        comment.setId(1);
        comment.setText("comment");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2023, 6, 6, 11, 0));

    }

    @Test
    void testAddComment() throws Exception {
        when(commentService.addComment(anyInt(), anyInt(), any())).thenReturn(comment);

        mvc.perform(post("/items/1/comment").content(TestConvert.asJsonString(commentDto)).header(HEADER_PARAM, 1).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created[0]", is(commentDto.getCreated().getYear())))
                .andExpect(jsonPath("$.created[1]", is(commentDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$.created[2]", is(commentDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$.created[3]", is(commentDto.getCreated().getHour())))
                .andExpect(jsonPath("$.created[4]", is(commentDto.getCreated().getMinute())));
    }
}
