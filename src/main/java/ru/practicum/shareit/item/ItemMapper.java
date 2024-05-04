package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemToRequestResponse;

import java.util.List;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface ItemMapper {

    Item toItem(ItemDto itemDto);

    @Mapping(target = "requestId", source = "item.request.id")
    ItemDto toDto(Item item);

    List<ItemDto> toDtoList(List<Item> items);

    @Mapping(target = "requestId", source = "item.request.id")
    ItemToRequestResponse toDtoItemToRequest(Item item);

    List<ItemToRequestResponse> toDtoListItemsRequest(List<Item> items);
}
