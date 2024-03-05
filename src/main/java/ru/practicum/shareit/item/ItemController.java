package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.exceptions.ForbiddenUpdateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDtoCreate itemDto,
                              @RequestHeader(name = "X-Sharer-User-Id") Integer userId) {
        User owner = userService.getData(userId);
        itemDto.setOwner(owner);
        Item item = itemMapper.toItem(itemDto);
        Item createdItem = itemService.create(item);
        log.info("Create item {}.", createdItem);
        return itemMapper.toItemDto(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Integer itemId,
                              @Valid @RequestBody ItemDtoUpdate itemDto,
                              @RequestHeader(name = "X-Sharer-User-Id") Integer userId) {
        Item item = itemMapper.toItem(itemDto);
        Item updatedItem;
        if (!Objects.equals(itemService.getData(itemId).getOwner().getId(), userId)) {
            throw new ForbiddenUpdateException(String.format("User with id = %s is not allowed to edit.", userId));
        } else {
            updatedItem = itemService.update(itemId, item);
            log.info("Update item {}.", updatedItem);
        }
        return itemMapper.toItemDto(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId) {
        Item item = itemService.getData(itemId);
        log.info("Get item with id = {}.", itemId);
        return itemMapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader(name = "X-Sharer-User-Id") Integer userId) {
        User owner = userService.getData(userId);
        List<Item> items = itemService.getAll(userId);
        log.info("Get all items by owner with id = {}.", userId);
        return itemListToItemDtoList(items);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String text) {
        List<Item> items = new ArrayList<>();
        if (!text.isBlank()) {
            items = itemService.search(text);
        }
        return itemListToItemDtoList(items);
    }

    private List<ItemDto> itemListToItemDtoList(List<Item> items) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = itemMapper.toItemDto(item);
            itemsDto.add(itemDto);
        }
        return itemsDto;
    }
}
