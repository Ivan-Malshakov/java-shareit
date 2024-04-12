package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentResearchDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader(name = "X-Sharer-User-Id") @Min(1) Integer userId) {
        log.info("Create item {}.", itemDto);
        return itemService.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable @Min(1) Integer itemId,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader(name = "X-Sharer-User-Id") @Min(1) Integer userId) {
        log.info("Update item {}.", itemDto);
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(name = "X-Sharer-User-Id") @Min(1) Integer userId,
                               @PathVariable @Min(1) Integer itemId) {
        log.info("Get item with id = {}.", itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader(name = "X-Sharer-User-Id") @Min(1) Integer userId) {
        log.info("Get all items by owner with id = {}.", userId);
        return itemService.getItemToUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(name = "X-Sharer-User-Id") @Min(1) Integer userId,
                                @RequestParam(name = "text") @Size(max = 100) String text) {
        log.info("Text search: {}.", text);
        return itemService.searchItem(text, userId);
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentResponseDto saveComment(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                          @PathVariable @Min(1) Integer itemId,
                                          @Valid @RequestBody CommentResearchDto researchDto) {
        log.info("Saving comment by user with id = " + userId + " item with id = " + itemId
                + " comment {}", researchDto);
        return itemService.saveComment(itemId, userId, researchDto);
    }
}
