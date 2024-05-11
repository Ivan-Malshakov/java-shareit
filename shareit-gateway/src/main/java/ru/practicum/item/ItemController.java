package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.CommentResearchDto;
import ru.practicum.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                           @RequestBody @NotNull @Valid ItemDto itemDto) {
        log.debug("Create item {}", itemDto);
        return itemClient.saveItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                          @PathVariable @Min(1) Integer itemId) {
        log.debug("Get item with id = {} for user with id = {}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemToUser(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId) {
        log.debug("Get all items by owner with id = {}", userId);
        return itemClient.getItemToUser(userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                             @RequestBody @NotNull ItemDto itemDto,
                                             @PathVariable @Min(1) Integer itemId) {
        log.debug("Update item {}", itemDto);
        return itemClient.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemToSearch(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                                  @RequestParam(defaultValue = "") String text) {
        log.debug("Text search: {}.", text);
        if (text.isBlank() || text.contains(" ")) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        return itemClient.searchItem(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                              @PathVariable @Min(1) Integer itemId,
                                              @NotNull @Valid @RequestBody CommentResearchDto researchDto) {
        log.debug("Saving comment by user with id = " + userId + " item with id = " + itemId
                + " comment {}", researchDto);
        return itemClient.saveComment(itemId, userId, researchDto);
    }
}
