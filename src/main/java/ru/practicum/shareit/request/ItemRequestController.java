package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestsResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestsResponseDto saveItemRequest(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                                   @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("Save item request {}", requestDto);
        return itemRequestService.saveItemRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestsResponseDto> getRequestsToUser(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId) {
        log.info("Get all user requests with id = {}", userId);
        return itemRequestService.getRequestsToUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestsResponseDto> getRequestsToOtherUsers(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false) @Min(1) Integer size) {
        log.info("Get all requests from other users by user with id = {}", userId);
        if (size == null) {
            return itemRequestService.getRequestsToOtherUsers(userId, from, Integer.MAX_VALUE);
        } else {
            return itemRequestService.getRequestsToOtherUsers(userId, from, size);
        }
    }

    @GetMapping("/{requestId}")
    public ItemRequestsResponseDto getRequestById(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                                  @PathVariable @Min(1) Integer requestId) {
        log.info("Get request by user with id = {}", userId);
        return itemRequestService.getRequest(userId, requestId);
    }
}
