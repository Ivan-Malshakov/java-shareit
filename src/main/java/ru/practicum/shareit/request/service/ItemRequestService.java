package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestsResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestsResponseDto saveItemRequest(Integer userId, ItemRequestDto request);

    List<ItemRequestsResponseDto> getRequestsToUser(Integer userId);

    List<ItemRequestsResponseDto> getRequestsToOtherUsers(Integer userId, Integer from, Integer size);

    ItemRequestsResponseDto getRequest(Integer userId, Integer requestId);

}
