package ru.practicum.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.item.dto.CommentResearchDto;
import ru.practicum.item.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveItem(ItemDto requestDto, Integer userId) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> saveComment(Integer itemId, Integer userId, CommentResearchDto requestDto) {
        return post("/" + itemId + "/comment", userId, requestDto);
    }

    public ResponseEntity<Object> getItem(Integer itemId, Integer userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemToUser(Integer userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItem(String text, Integer userId) {
        return get("/search?text=" + text, userId);
    }

    public ResponseEntity<Object> updateItem(ItemDto requestDto, Integer userId, Integer itemId) {
        return patch("/" + itemId, userId, requestDto);
    }
}
