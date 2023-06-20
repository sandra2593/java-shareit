package ru.practicum.shareit.user;

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
import ru.practicum.shareit.exception.ExceptionController;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController userController;

    private MockMvc mvc;

    private UserDto userDto;

    private ResponseEntity<Object> userResponse;

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(ExceptionController.class).build();

        userDto = UserDto.builder().id(1).name("user").email("user@mail.com").build();

        userResponse = new ResponseEntity<>(TestConvert.asJsonString(userDto), HttpStatus.OK);
    }


    @Test
    void testCreate() throws Exception {
        when(userClient.create(any())).thenReturn(userResponse);

        mvc.perform(post("/users").content(TestConvert.asJsonString(userDto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

    }

    @Test
    void testCreateEmptyEmail() throws Exception {
        userDto.setName("name");
        userResponse = new ResponseEntity<>(Map.of("email", "Email не должен быть пустым"), HttpStatus.BAD_REQUEST);
        when(userClient.create(any(UserDto.class))).thenReturn(userResponse);

        mvc.perform(post("/users").content(TestConvert.asJsonString(userDto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("email")))
                .andExpect(jsonPath("$.email", is("Email не должен быть пустым")));
    }

    @Test
    void testUpdate() throws Exception {
        userDto.setName("updateName");
        userResponse = new ResponseEntity<>(TestConvert.asJsonString(userDto), HttpStatus.OK);
        when(userClient.update(anyInt(), any())).thenReturn(userResponse);

        mvc.perform(patch("/users/" + 1).content(TestConvert.asJsonString(userDto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));


    }

    @Test
    void testUpdateUserDoesntExist() throws Exception {
        userResponse = new ResponseEntity<>(Map.of("message", "нет пользователя с id 1"), HttpStatus.NOT_FOUND);
        when(userClient.update(anyInt(), any())).thenReturn(userResponse);

        mvc.perform(patch("/users/" + 1).content(TestConvert.asJsonString(userDto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("нет пользователя с id 1")));
    }

    @Test
    void testDelete() throws Exception {
        userResponse = new ResponseEntity<>(null, HttpStatus.OK);
        when(userClient.delete(anyInt())).thenReturn(userResponse);

        mvc.perform(delete("/users/" + 1).content(TestConvert.asJsonString(userDto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUserDoesntExist() throws Exception {
        userResponse = new ResponseEntity<>(Map.of("message", "нет пользователя с id 1"), HttpStatus.NOT_FOUND);
        when(userClient.delete(anyInt())).thenReturn(userResponse);

        mvc.perform(delete("/users/" + 1).content(TestConvert.asJsonString(userDto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("нет пользователя с id 1")));
    }

    @Test
    void testGetUserById() throws Exception {
        when(userClient.getUserById(anyInt())).thenReturn(userResponse);

        mvc.perform(get("/users/" + 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }


    @Test
    void testGetUserByIdUserDoesntExist() throws Exception {
        userResponse = new ResponseEntity<>(Map.of("message", "нет пользователя с id 1"), HttpStatus.NOT_FOUND);
        when(userClient.getUserById(anyInt())).thenReturn(userResponse);

        mvc.perform(get("/users/" + 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("нет пользователя с id 1")));
    }

    @Test
    void testGetAll() throws Exception {
        userResponse = new ResponseEntity<>(TestConvert.asJsonString(List.of(userDto)), HttpStatus.OK);
        when(userClient.getAll()).thenReturn(userResponse);

        mvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }

    @Test
    void testGetAllNoUsers() throws Exception {
        userResponse = new ResponseEntity<>(TestConvert.asJsonString(List.of()), HttpStatus.OK);
        when(userClient.getAll()).thenReturn(userResponse);

        mvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

}
