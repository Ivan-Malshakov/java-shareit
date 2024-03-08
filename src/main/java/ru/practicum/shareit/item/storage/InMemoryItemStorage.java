package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.Item;

import java.util.*;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Integer, Item> storage = new HashMap<>();
    private Integer generatedId = 0;


    @Override
    public Item create(Item item) {
        item.setId(++generatedId);
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Integer id, Item item) {
        if (!storage.containsKey(id)) {
            throw new DataNotFoundException(String.format("Item with id = %s not found.", id));
        }
        return updateItemInStorage(id, item);
    }

    @Override
    public Item getData(Integer itemId) {
        if (!storage.containsKey(itemId)) {
            throw new DataNotFoundException(String.format("Item with id = %s not found.", itemId));
        }
        return storage.get(itemId);
    }

    @Override
    public List<Item> getAll(Integer userId) {
        List<Item> items = new ArrayList<>();
        for (Item item : storage.values()) {
            if (Objects.equals(item.getOwner().getId(), userId)) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public List<Item> search(String text) {
        List<Item> items = new ArrayList<>();
        for (Item item : storage.values()) {
            if (item.getName().trim().toLowerCase().contains(text.trim().toLowerCase())
                    || item.getDescription().trim().toLowerCase().contains(text.trim().toLowerCase())
                    && item.getAvailable()) {
                items.add(item);
            }
        }
        return items;
    }

    private Item updateItemInStorage(Integer itemId, Item item) {
        Item oldItem = storage.get(itemId);
        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setOwner(oldItem.getOwner());

        if (item.getName() == null) {
            updatedItem.setName(oldItem.getName());
        } else {
            updatedItem.setName(item.getName());
        }

        if (item.getDescription() == null) {
            updatedItem.setDescription(oldItem.getDescription());
        } else {
            updatedItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() == null) {
            updatedItem.setAvailable(oldItem.getAvailable());
        } else {
            updatedItem.setAvailable(item.getAvailable());
        }

        storage.put(itemId, updatedItem);
        return updatedItem;
    }
}
