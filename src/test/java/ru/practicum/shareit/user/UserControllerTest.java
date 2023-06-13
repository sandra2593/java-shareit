package ru.practicum.shareit.user;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.controller.ExceptionController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mvc;

    private User user;

    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(ExceptionController.class).build();

        user = new User();
        user.setId(1);
        user.setName("user");
        user.setEmail("user@mail.com");

        userDto = UserDto.builder().id(1).name("user").email("user@mail.com").build();
    }


    @Test
    void testCreate() throws Exception {
        when(userService.create(any(User.class))).thenReturn(user);

        mvc.perform(post("/users").content(TestConvert.asJsonString(userDto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

    }

    @Test
    void testCreateEmptyEmail() throws Exception {
        userDto = UserDto.builder().id(1).name("user").build();

        mvc.perform(post("/users").content(TestConvert.asJsonString(userDto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        when(userService.update(anyInt(), any(User.class))).thenReturn(user);

        mvc.perform(patch("/users/" + 1).content(TestConvert.asJsonString(userDto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));


    }

    @Test
    void testUpdateUserDoesntExist() throws Exception {
        when(userService.update(anyInt(), any(User.class))).thenThrow(new NotFoundException("нет пользователя с id " + 1));

        mvc.perform(patch("/users/" + 1).content(TestConvert.asJsonString(userDto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("нет пользователя с id 1")));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(userService).delete(anyInt());

        mvc.perform(delete("/users/" + 1).content(TestConvert.asJsonString(userDto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUserDoesntExist() throws Exception {
        doThrow(new NotFoundException("нет пользователя с id " + 1)).when(userService).delete(anyInt());

        mvc.perform(delete("/users/" + 1).content(TestConvert.asJsonString(userDto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("нет пользователя с id 1")));
    }

    @Test
    void testGetUserById() throws Exception {
        when(userService.getUserById(anyInt())).thenReturn(user);

        mvc.perform(get("/users/" + 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }


    @Test
    void testGetUserByIdUserDoesntExist() throws Exception {
        when(userService.getUserById(anyInt())).thenThrow(new NotFoundException("нет пользователя с id " + 1));

        mvc.perform(get("/users/" + 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("нет пользователя с id 1")));
    }

    @Test
    void testGetAll() throws Exception {
        when(userService.getAll()).thenReturn(List.of(user));

        mvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }

    @Test
    void testGetAllNoUsers() throws Exception {
        when(userService.getAll()).thenReturn(List.of());

        mvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

}
