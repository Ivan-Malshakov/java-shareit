package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.exceptions.ForbiddenUpdateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDtoCreate itemDto,
                              @RequestHeader(name = "X-Sharer-User-Id") @Min(1) Integer userId)
            throws ConstraintViolationException {
        if (userId < 1) {
            throw new ConstraintViolationException("Invalid userId", new HashSet<>());
        }

        User owner = userService.getData(userId);
        itemDto.setOwner(owner);
        Item item = itemMapper.toItem(itemDto);
        Item createdItem = itemService.create(item);
        log.info("Create item {}.", createdItem);
        return itemMapper.toItemDto(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable @Min(1) Integer itemId,
                              @Valid @RequestBody ItemDtoUpdate itemDto,
                              @RequestHeader(name = "X-Sharer-User-Id") @Min(1) Integer userId)
            throws ConstraintViolationException {
        if (userId < 1) {
            throw new ConstraintViolationException("Invalid userId", new HashSet<>());
        }
        if (itemId < 1) {
            throw new ConstraintViolationException("Invalid itemId", new HashSet<>());
        }

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
    public ItemDto getItemById(@PathVariable @Min(1) Integer itemId) throws ConstraintViolationException {
        if (itemId < 1) {
            throw new ConstraintViolationException("Invalid itemId", new HashSet<>());
        }

        Item item = itemService.getData(itemId);
        log.info("Get item with id = {}.", itemId);
        return itemMapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader(name = "X-Sharer-User-Id") @Min(1) Integer userId)
            throws ConstraintViolationException {
        if (userId < 1) {
            throw new ConstraintViolationException("Invalid userId", new HashSet<>());
        }

        User owner = userService.getData(userId);
        List<Item> items = itemService.getAll(userId);
        log.info("Get all items by owner with id = {}.", userId);
        return itemListToItemDtoList(items);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") @Size(max = 100) String text)
            throws ConstraintViolationException {
        if (text.length() > 100) {
            throw new ConstraintViolationException("Too many characters (max = 100)", new HashSet<>());
        }

        List<Item> items = new ArrayList<>();
        if (!text.isBlank()) {
            items = itemService.search(text);
        }
        log.info("Text search: {}.", text);
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
