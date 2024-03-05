package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage storage;

    @Override
    public Item create(Item item) {
        return storage.create(item);
    }

    @Override
    public Item update(Integer itemId, Item item) {
        return storage.update(itemId, item);
    }

    @Override
    public Item getData(Integer itemId) {
        return storage.getData(itemId);
    }

    @Override
    public List<Item> getAll(Integer userId) {
        return storage.getAll(userId);
    }

    @Override
    public List<Item> search(String text) {
        return storage.search(text);
    }

}
