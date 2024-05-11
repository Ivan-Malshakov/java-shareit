package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> saveItemRequest(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                                  @RequestBody @NotNull @Valid ItemRequestDto requestDto) {
        log.debug("Save item request {}", requestDto);
        return itemRequestClient.saveItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsToUser(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId) {
        log.debug("Get all user requests with id = {}", userId);
        return itemRequestClient.getRequestsToUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsToAnotherUsers(
            @RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false) @Min(1) Integer size) {
        if (size == null) {
            size = Integer.MAX_VALUE;
        }
        log.debug("Get all requests from other users by user with id = {}", userId);
        return itemRequestClient.getRequestsToAnotherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") @Min(1) Integer userId,
                                                 @PathVariable @Min(1) Integer requestId) {
        log.debug("Get request by user with id = {}", userId);
        return itemRequestClient.getRequest(userId, requestId);
    }
}
