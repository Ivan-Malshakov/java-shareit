package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentResearchDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService service;
    @Autowired
    private MockMvc mvc;

    @Test
    void saveItemShouldBeOk() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Item 1", "Desc 1", true,
                null, null, new ArrayList<>(), 3);

        when(service.saveItem(any(ItemDto.class), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemResponse = invocationOnMock.getArgument(0, ItemDto.class);
                    itemResponse.setId(1);
                    return itemResponse;
                });

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking())))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$.comments", is(itemDto.getComments())))
                .andExpect(jsonPath("$.requestId", is(3), Integer.class));
    }

    @Test
    void saveItemShouldThrowValidationExceptionWithInvalidName() throws Exception {
        ItemDto itemDto = new ItemDto(null, "", "Desc 1", true,
                null, null, new ArrayList<>(), 3);

        when(service.saveItem(any(ItemDto.class), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemResponse = invocationOnMock.getArgument(0, ItemDto.class);
                    itemResponse.setId(1);
                    return itemResponse;
                });

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveItemShouldThrowValidationExceptionWithInvalidDescription() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Item 1", "", true,
                null, null, new ArrayList<>(), 3);

        when(service.saveItem(any(ItemDto.class), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemResponse = invocationOnMock.getArgument(0, ItemDto.class);
                    itemResponse.setId(1);
                    return itemResponse;
                });

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveItemShouldThrowValidationExceptionWithInvalidAvailable() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Item 1", "Desc 1", null,
                null, null, new ArrayList<>(), 3);

        when(service.saveItem(any(ItemDto.class), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemResponse = invocationOnMock.getArgument(0, ItemDto.class);
                    itemResponse.setId(1);
                    return itemResponse;
                });

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemShouldBeOk() throws Exception {
        when(service.getItem(anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemDto = new ItemDto(null, "Item 1", "Desc 1", true,
                            null, null, new ArrayList<>(), 1);
                    itemDto.setId(invocationOnMock.getArgument(1, Integer.class));
                    return itemDto;
                });

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is("Item 1")))
                .andExpect(jsonPath("$.description", is("Desc 1")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.comments", is(new ArrayList())))
                .andExpect(jsonPath("$.requestId", is(1), Integer.class));
    }

    @Test
    void getItemToUserShouldBeOk() throws Exception {
        List<ItemDto> items = new ArrayList<>();
        items.add(new ItemDto(1, "Item 1", "Desc 1", true,
                null, null, new ArrayList<>(), 1));
        items.add(new ItemDto(2, "Item 2", "Desc 2", true,
                null, null, new ArrayList<>(), 1));
        items.add(new ItemDto(3, "Item 3", "Desc 3", true,
                null, null, new ArrayList<>(), 1));

        when(service.getItemToUser(anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[0].id", is(items.get(0).getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(items.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(items.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(items.get(0).getAvailable())))
                .andExpect(jsonPath("$[0].nextBooking", is(items.get(0).getNextBooking())))
                .andExpect(jsonPath("$[0].lastBooking", is(items.get(0).getLastBooking())))
                .andExpect(jsonPath("$[0].comments", is(items.get(0).getComments())))
                .andExpect(jsonPath("$[0].requestId", is(items.get(0).getRequestId()), Integer.class))
                .andExpect(jsonPath("$[1].id", is(items.get(1).getId()), Integer.class))
                .andExpect(jsonPath("$[1].name", is(items.get(1).getName())))
                .andExpect(jsonPath("$[1].description", is(items.get(1).getDescription())))
                .andExpect(jsonPath("$[1].available", is(items.get(1).getAvailable())))
                .andExpect(jsonPath("$[1].nextBooking", is(items.get(1).getNextBooking())))
                .andExpect(jsonPath("$[1].lastBooking", is(items.get(1).getLastBooking())))
                .andExpect(jsonPath("$[1].comments", is(items.get(1).getComments())))
                .andExpect(jsonPath("$[1].requestId", is(items.get(1).getRequestId()), Integer.class))
                .andExpect(jsonPath("$[2].id", is(items.get(2).getId()), Integer.class))
                .andExpect(jsonPath("$[2].name", is(items.get(2).getName())))
                .andExpect(jsonPath("$[2].description", is(items.get(2).getDescription())))
                .andExpect(jsonPath("$[2].available", is(items.get(2).getAvailable())))
                .andExpect(jsonPath("$[2].nextBooking", is(items.get(2).getNextBooking())))
                .andExpect(jsonPath("$[2].lastBooking", is(items.get(2).getLastBooking())))
                .andExpect(jsonPath("$[2].comments", is(items.get(2).getComments())))
                .andExpect(jsonPath("$[2].requestId", is(items.get(2).getRequestId()), Integer.class));
    }

    @Test
    void updateItemShouldBeOk() throws Exception {
        ItemDto itemDto = new ItemDto(null, "New item 1", null,
                null, null, null, null, null);

        when(service.updateItem(any(ItemDto.class), anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemResponse = invocationOnMock.getArgument(0, ItemDto.class);
                    itemResponse.setId(invocationOnMock.getArgument(2, Integer.class));
                    itemResponse.setDescription("Desc 1");
                    itemResponse.setAvailable(true);
                    itemResponse.setComments(new ArrayList<>());
                    return itemResponse;
                });

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is("Desc 1")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking())))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$.comments", is(new ArrayList())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void getItemToSearchShouldBeOk() throws Exception {
        ItemDto itemDto1 = new ItemDto(1, "Item 1", "Desc 1",
                false, null, null, new ArrayList<>(), null);
        ItemDto itemDto2 = new ItemDto(2, "Item 2", "Desc 2",
                true, null, null, new ArrayList<>(), null);

        when(service.searchItem(anyString(), anyInt()))
                .thenReturn(List.of(itemDto1, itemDto2));

        mvc.perform(get("/items/search?text=пре")
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemDto1.getNextBooking())))
                .andExpect(jsonPath("$[0].lastBooking", is(itemDto1.getLastBooking())))
                .andExpect(jsonPath("$[0].comments", is(itemDto1.getComments())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto1.getRequestId()), Integer.class))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getId()), Integer.class))
                .andExpect(jsonPath("$[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDto2.getAvailable())))
                .andExpect(jsonPath("$[1].nextBooking", is(itemDto2.getNextBooking())))
                .andExpect(jsonPath("$[1].lastBooking", is(itemDto2.getLastBooking())))
                .andExpect(jsonPath("$[1].comments", is(itemDto2.getComments())));
    }

    @Test
    void saveCommentShouldThrowValidationExceptionWithInvalidText() throws Exception {
        CommentResearchDto commentResearchDto = new CommentResearchDto();
        commentResearchDto.setText("");

        when(service.saveComment(anyInt(), anyInt(), any(CommentResearchDto.class)))
                .thenAnswer(invocationOnMock -> {
                    CommentResponseDto commentResponseDto = invocationOnMock.getArgument(2,
                            CommentResponseDto.class);
                    return commentResponseDto;
                });

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentResearchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveCommentShouldBeOk() throws Exception {
        CommentResearchDto commentResearchDto = new CommentResearchDto();
        commentResearchDto.setText("Comment 1");

        when(service.saveComment(anyInt(), anyInt(), any(CommentResearchDto.class)))
                .thenAnswer(invocationOnMock -> {
                    CommentResearchDto researchDto = invocationOnMock.getArgument(2,
                            CommentResearchDto.class);
                    CommentResponseDto commentResponseDto = new CommentResponseDto();
                    commentResponseDto.setId(1);
                    commentResponseDto.setText(researchDto.getText());
                    commentResponseDto.setCreated(researchDto.getCreated());
                    commentResponseDto.setAuthorName("Author");
                    return commentResponseDto;
                });

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentResearchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.text", is(commentResearchDto.getText())))
                .andExpect(jsonPath("$.created", is(commentResearchDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.authorName", is("Author")));
    }
}
