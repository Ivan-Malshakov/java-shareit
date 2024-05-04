package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestsResponseDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = ItemRequestMapper.class)
public interface ItemRequestMapper {
    ItemRequest toRequest(ItemRequestDto itemRequestDto);

    ItemRequestsResponseDto toDto(ItemRequest itemRequest);

    List<ItemRequestsResponseDto> toDtoList(List<ItemRequest> itemRequests);
}
