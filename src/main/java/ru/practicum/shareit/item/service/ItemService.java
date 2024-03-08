package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item);

    Item update(Integer itemId, Item item);

    Item getData(Integer itemId);

    List<Item> getAll(Integer userId);

    List<Item> search(String text);
}
