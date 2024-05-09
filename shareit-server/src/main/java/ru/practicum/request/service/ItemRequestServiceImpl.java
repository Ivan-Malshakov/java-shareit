package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.exceptions.DataNotFoundException;
import ru.practicum.item.ItemMapper;
import ru.practicum.item.dto.ItemToRequestResponse;
import ru.practicum.item.storage.db.JpaItemRepository;
import ru.practicum.request.ItemRequest;
import ru.practicum.request.ItemRequestMapper;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestsResponseDto;
import ru.practicum.request.storage.JpaItemRequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;
import ru.practicum.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final JpaItemRequestRepository jpaItemRequestRepository;
    private final JpaItemRepository jpaItemRepository;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ItemRequestsResponseDto saveItemRequest(Integer userId, ItemRequestDto request) {
        User user = userMapper.toUser(userService.getData(userId));
        ItemRequest itemRequest = itemRequestMapper.toRequest(request);
        itemRequest.setRequestor(user);
        return itemRequestMapper.toDto(jpaItemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestsResponseDto> getRequestsToUser(Integer userId) {
        User user = userMapper.toUser(userService.getData(userId));
        List<ItemRequestsResponseDto> requests = itemRequestMapper
                .toDtoList(jpaItemRequestRepository.findItemRequestByRequestor_IdOrderByCreatedDesc(user.getId()));
        return getItemRequestsResponseDto(requests);
    }

    private List<ItemRequestsResponseDto> getItemRequestsResponseDto(List<ItemRequestsResponseDto> requests) {
        List<Integer> requestIds = requests.stream()
                .map(ItemRequestsResponseDto::getId)
                .collect(toList());
        Map<Integer, List<ItemToRequestResponse>> itemsByRequest = jpaItemRepository.findByRequest_IdIn(requestIds)
                .stream()
                .map(itemMapper::toDtoItemToRequest)
                .collect(groupingBy(ItemToRequestResponse::getRequestId, toList()));
        if (!itemsByRequest.isEmpty()) {
            requests.stream()
                    .peek(r -> r.setItems(itemsByRequest.getOrDefault(r.getId(), Collections.emptyList())))
                    .collect(toList());
        }
        return requests;
    }

    @Override
    public List<ItemRequestsResponseDto> getRequestsToOtherUsers(Integer userId, Integer from, Integer size) {
        User user = userMapper.toUser(userService.getData(userId));
        List<ItemRequestsResponseDto> requests = itemRequestMapper.toDtoList(jpaItemRequestRepository
                        .findItemRequestNotByRequestor_IdOrderByCreatedDesc(user.getId())).stream()
                .skip(from)
                .limit(size)
                .collect(toList());
        return getItemRequestsResponseDto(requests);
    }

    @Override
    public ItemRequestsResponseDto getRequest(Integer userId, Integer requestId) {
        userService.getData(userId);
        if (jpaItemRequestRepository.findById(requestId).isEmpty()) {
            log.warn("Request with id = " + requestId + " not found");
            throw new DataNotFoundException("Request with id = " + requestId + " not found");
        }
        ItemRequestsResponseDto request = itemRequestMapper.toDto(jpaItemRequestRepository.findById(requestId).get());
        request.setItems(itemMapper.toDtoListItemsRequest(jpaItemRepository.findByRequest_Id(requestId)));
        return request;
    }
}
