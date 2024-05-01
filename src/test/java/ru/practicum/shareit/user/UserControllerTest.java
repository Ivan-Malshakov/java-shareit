package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @InjectMocks
    private UserController controller;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private UserService service;
    @Autowired
    private MockMvc mvc;

    @Test
    void getAllUsersShouldBeOk() throws Exception {
        when(service.getAll())
                .thenAnswer(invocationOnMock -> {
                    List<User> usersResponse = new ArrayList<>();
                    usersResponse.add(new User(1, "User 1", "user1@yandex.ru"));
                    usersResponse.add(new User(2, "User 2", "user2@yandex.ru"));
                    usersResponse.add(new User(1, "User 2", "user2@yandex.ru"));
                    return usersResponse;
                });

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void getUserByIdShouldBeOk() throws Exception {

        UserDto userResponse = new UserDto(1, "User 1", "user1@yandex.ru");

        when(service.getData(anyInt())).thenReturn(userResponse);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is("User 1")))
                .andExpect(jsonPath("$.email", is("user1@yandex.ru")));
    }

    @Test
    void createUserShouldBeOk() throws Exception {
        UserDto userResearch = new UserDto(1, "User 1", "user1@yandex.ru");

        when(service.create(any(UserDto.class))).thenReturn(userResearch);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userResearch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(userResearch.getName())))
                .andExpect(jsonPath("$.email", is(userResearch.getEmail())));
    }

    @Test
    void createUserShouldThrowValidationExceptionWithInvalidEmail() throws Exception {
        UserDto userResearch = new UserDto(1, "User 1", "user1yandex.ru");

        when(service.create(any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto userDto = invocationOnMock.getArgument(0, UserDto.class);
                    User userResponse = new User();
                    userResponse.setId(1);
                    userResponse.setName(userDto.getName());
                    userResponse.setEmail(userDto.getEmail());
                    return userResponse;
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userResearch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserShouldBeOk() throws Exception {
        UserDto userResearch = new UserDto(1, "New user 1", "user1@yandex.ru");

        when(service.update(anyInt(), any(UserDto.class))).thenReturn(userResearch);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userResearch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(userResearch.getName())))
                .andExpect(jsonPath("$.email", is(userResearch.getEmail())));
    }

    @Test
    void deleteUserByIdShouldBeOk() throws Exception {
        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
