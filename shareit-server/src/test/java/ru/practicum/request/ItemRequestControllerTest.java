package ru.practicum.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.item.dto.ItemToRequestResponse;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestsResponseDto;
import ru.practicum.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService service;
    @Autowired
    private MockMvc mvc;

    @Test
    void saveItemRequestShouldThrowValidationExceptionWithInvalidItemRequestDto() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();

        when(service.saveItemRequest(anyInt(), any(ItemRequestDto.class)))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDto requestDto1 = invocationOnMock.getArgument(1, ItemRequestDto.class);
                    ItemRequestsResponseDto responseDto = new ItemRequestsResponseDto();
                    responseDto.setId(1);
                    responseDto.setCreated(requestDto1.getCreated());
                    responseDto.setDescription(requestDto1.getDescription());
                    return responseDto;
                });

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveItemRequestShouldThrowValidationExceptionWithInvalidUserIdInHeader() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Request 1");
        Integer userId = 0;

        when(service.saveItemRequest(anyInt(), any(ItemRequestDto.class)))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDto requestDto1 = invocationOnMock.getArgument(1, ItemRequestDto.class);
                    ItemRequestsResponseDto responseDto = new ItemRequestsResponseDto();
                    responseDto.setId(1);
                    responseDto.setCreated(requestDto1.getCreated());
                    responseDto.setDescription(requestDto1.getDescription());
                    return responseDto;
                });

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveItemRequestShouldThrowValidationExceptionWithoutUserIdInHeader() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Request 1");

        when(service.saveItemRequest(anyInt(), any(ItemRequestDto.class)))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDto requestDto1 = invocationOnMock.getArgument(1, ItemRequestDto.class);
                    ItemRequestsResponseDto responseDto = new ItemRequestsResponseDto();
                    responseDto.setId(1);
                    responseDto.setCreated(requestDto1.getCreated());
                    responseDto.setDescription(requestDto1.getDescription());
                    return responseDto;
                });

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void saveItemRequestShouldBeOk() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Request 1");

        when(service.saveItemRequest(anyInt(), any(ItemRequestDto.class)))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDto requestDto1 = invocationOnMock.getArgument(1, ItemRequestDto.class);
                    ItemRequestsResponseDto responseDto = new ItemRequestsResponseDto();
                    responseDto.setId(1);
                    responseDto.setCreated(requestDto1.getCreated());
                    responseDto.setDescription(requestDto1.getDescription());
                    return responseDto;
                });

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.items", is(new ArrayList<>())));

    }

    @Test
    void getRequestsToUserShouldBeOk() throws Exception {
        ItemRequestsResponseDto responseDto1 = new ItemRequestsResponseDto(1, "Desc 1",
                LocalDateTime.now(), new ArrayList<>());
        ItemRequestsResponseDto responseDto2 = new ItemRequestsResponseDto(2, "Desc 2",
                LocalDateTime.now(), new ArrayList<>());

        when(service.getRequestsToUser(anyInt())).thenReturn(List.of(responseDto1, responseDto2));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(responseDto1.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(responseDto1.getDescription())))
                .andExpect(jsonPath("$[0].created", is(responseDto1.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].items", is(responseDto1.getItems())))
                .andExpect(jsonPath("$[1].id", is(responseDto2.getId()), Integer.class))
                .andExpect(jsonPath("$[1].description", is(responseDto2.getDescription())))
                .andExpect(jsonPath("$[1].created", is(responseDto2.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[1].items", is(responseDto2.getItems())));
    }

    @Test
    void getRequestsToUserShouldThrowValidationExceptionWithInvalidUserIdInHeader() throws Exception {
        ItemRequestsResponseDto responseDto1 = new ItemRequestsResponseDto(1, "Desc 1",
                LocalDateTime.now(), new ArrayList<>());
        ItemRequestsResponseDto responseDto2 = new ItemRequestsResponseDto(2, "Desc 2",
                LocalDateTime.now(), new ArrayList<>());
        Integer userId = 0;

        when(service.getRequestsToUser(anyInt())).thenReturn(List.of(responseDto1, responseDto2));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestsToUserShouldThrowValidationExceptionWithoutUserIdInHeader() throws Exception {
        ItemRequestsResponseDto responseDto1 = new ItemRequestsResponseDto(1, "Desc 1",
                LocalDateTime.now(), new ArrayList<>());
        ItemRequestsResponseDto responseDto2 = new ItemRequestsResponseDto(2, "Desc 2",
                LocalDateTime.now(), new ArrayList<>());

        when(service.getRequestsToUser(anyInt())).thenReturn(List.of(responseDto1, responseDto2));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getRequestsToOtherUsersShouldBeOkWithSizeNull() throws Exception {
        ItemRequestsResponseDto responseDto1 = new ItemRequestsResponseDto(1, "Desc 1",
                LocalDateTime.now(), new ArrayList<>());
        ItemRequestsResponseDto responseDto2 = new ItemRequestsResponseDto(2, "Desc 2",
                LocalDateTime.now(), new ArrayList<>());
        ItemToRequestResponse item = new ItemToRequestResponse(1, "Item 1", "Desc 1",
                true, 2);
        responseDto2.setItems(List.of(item));

        when(service.getRequestsToOtherUsers(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto1, responseDto2));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(responseDto1.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(responseDto1.getDescription())))
                .andExpect(jsonPath("$[0].created", is(responseDto1.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].items", is(responseDto1.getItems())))
                .andExpect(jsonPath("$[1].id", is(responseDto2.getId()), Integer.class))
                .andExpect(jsonPath("$[1].description", is(responseDto2.getDescription())))
                .andExpect(jsonPath("$[1].created", is(responseDto2.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[1].items.size()", is(responseDto2.getItems().size())))
                .andExpect(jsonPath("$[1].items.[0].name", is(responseDto2.getItems().get(0).getName())))
                .andExpect(jsonPath("$[1].items.[0].id",
                        is(responseDto2.getItems().get(0).getId()), Integer.class))
                .andExpect(jsonPath("$[1].items.[0].description",
                        is(responseDto2.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[1].items.[0].available",
                        is(responseDto2.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[1].items.[0].requestId",
                        is(responseDto2.getItems().get(0).getRequestId()), Integer.class));
    }

    @Test
    void getRequestsToOtherUsersShouldThrowValidationExceptionWithInvalidSize() throws Exception {
        ItemRequestsResponseDto responseDto1 = new ItemRequestsResponseDto(1, "Desc 1",
                LocalDateTime.now(), new ArrayList<>());
        ItemRequestsResponseDto responseDto2 = new ItemRequestsResponseDto(2, "Desc 2",
                LocalDateTime.now(), new ArrayList<>());
        ItemToRequestResponse item = new ItemToRequestResponse(1, "Item 1", "Desc 1",
                true, 2);
        responseDto2.setItems(List.of(item));

        when(service.getRequestsToOtherUsers(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto1, responseDto2));

        mvc.perform(get("/requests/all?from=0&size=-1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestsToOtherUsersShouldThrowValidationExceptionWithInvalidFrom() throws Exception {
        ItemRequestsResponseDto responseDto1 = new ItemRequestsResponseDto(1, "Desc 1",
                LocalDateTime.now(), new ArrayList<>());
        ItemRequestsResponseDto responseDto2 = new ItemRequestsResponseDto(2, "Desc 2",
                LocalDateTime.now(), new ArrayList<>());
        ItemToRequestResponse item = new ItemToRequestResponse(1, "Item 1", "Desc 1",
                true, 2);
        responseDto2.setItems(List.of(item));

        when(service.getRequestsToOtherUsers(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto1, responseDto2));

        mvc.perform(get("/requests/all?from=-1&size=2")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestsToOtherUsersShouldThrowValidationExceptionWithInvalidUserIdInHeader() throws Exception {
        ItemRequestsResponseDto responseDto1 = new ItemRequestsResponseDto(1, "Desc 1",
                LocalDateTime.now(), new ArrayList<>());
        ItemRequestsResponseDto responseDto2 = new ItemRequestsResponseDto(2, "Desc 2",
                LocalDateTime.now(), new ArrayList<>());
        ItemToRequestResponse item = new ItemToRequestResponse(1, "Item 1", "Desc 1",
                true, 2);
        responseDto2.setItems(List.of(item));
        Integer userId = 0;

        when(service.getRequestsToOtherUsers(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto1, responseDto2));

        mvc.perform(get("/requests/all?from=1&size=1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestsToOtherUsersShouldThrowValidationExceptionWithoutUserIdInHeader() throws Exception {
        ItemRequestsResponseDto responseDto1 = new ItemRequestsResponseDto(1, "Desc 1",
                LocalDateTime.now(), new ArrayList<>());
        ItemRequestsResponseDto responseDto2 = new ItemRequestsResponseDto(2, "Desc 2",
                LocalDateTime.now(), new ArrayList<>());
        ItemToRequestResponse item = new ItemToRequestResponse(1, "Item 1", "Desc 1",
                true, 2);
        responseDto2.setItems(List.of(item));

        when(service.getRequestsToOtherUsers(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto1, responseDto2));

        mvc.perform(get("/requests/all?from=1&size=1")
                        .header("X-Sharer-User-Id", "")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getRequestsToOtherUsersShouldBeOk() throws Exception {
        ItemRequestsResponseDto responseDto1 = new ItemRequestsResponseDto(1, "Desc 1",
                LocalDateTime.now(), new ArrayList<>());
        ItemRequestsResponseDto responseDto2 = new ItemRequestsResponseDto(2, "Desc 2",
                LocalDateTime.now(), new ArrayList<>());
        ItemToRequestResponse item = new ItemToRequestResponse(1, "Item 1", "Desc 1",
                true, 2);
        responseDto2.setItems(List.of(item));

        when(service.getRequestsToOtherUsers(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto2));

        mvc.perform(get("/requests/all?from=1&size=1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(responseDto2.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(responseDto2.getDescription())))
                .andExpect(jsonPath("$[0].created", is(responseDto2.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].items.size()", is(responseDto2.getItems().size())))
                .andExpect(jsonPath("$[0].items.[0].name", is(responseDto2.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items.[0].id",
                        is(responseDto2.getItems().get(0).getId()), Integer.class))
                .andExpect(jsonPath("$[0].items.[0].description",
                        is(responseDto2.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items.[0].available",
                        is(responseDto2.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items.[0].requestId",
                        is(responseDto2.getItems().get(0).getRequestId()), Integer.class));
    }

    @Test
    void getRequestByIdShouldBeOk() throws Exception {
        ItemRequestsResponseDto responseDto = new ItemRequestsResponseDto(1, "Desc 1",
                LocalDateTime.now(), new ArrayList<>());

        when(service.getRequest(anyInt(), anyInt())).thenReturn(responseDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$.created", is(responseDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.items", is(responseDto.getItems())));
    }

    @Test
    void getRequestByIdShouldThrowValidationExceptionWithInvalidUserIdInHeader() throws Exception {
        ItemRequestsResponseDto responseDto = new ItemRequestsResponseDto(1, "Desc 1",
                LocalDateTime.now(), new ArrayList<>());
        Integer userId = 0;

        when(service.getRequest(anyInt(), anyInt())).thenReturn(responseDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestByIdShouldThrowValidationExceptionWithoutUserIdInHeader() throws Exception {
        ItemRequestsResponseDto responseDto = new ItemRequestsResponseDto(1, "Desc 1",
                LocalDateTime.now(), new ArrayList<>());

        when(service.getRequest(anyInt(), anyInt())).thenReturn(responseDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getRequestByIdShouldThrowValidationExceptionWithInvalidRequestId() throws Exception {
        ItemRequestsResponseDto responseDto = new ItemRequestsResponseDto(1, "Desc 1",
                LocalDateTime.now(), new ArrayList<>());

        when(service.getRequest(anyInt(), anyInt())).thenReturn(responseDto);

        mvc.perform(get("/requests/0")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
