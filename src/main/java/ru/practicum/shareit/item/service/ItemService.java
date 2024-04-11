package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentResearchDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto request, Integer userId);

    ItemDto updateItem(ItemDto request, Integer userId, Integer itemId);

    ItemDto getItem(Integer id, Integer userId);

    List<ItemDto> getItemToUser(Integer userId);

    List<ItemDto> searchItem(String search, Integer userId);

    Item getItemToBooking(Integer id);

    CommentResponseDto saveComment(Integer itemId, Integer userId, CommentResearchDto research);

}
