package ru.practicum.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.BookingMapper;
import ru.practicum.booking.BookingStatus;
import ru.practicum.booking.dto.BookingResponseToItemDto;
import ru.practicum.booking.storage.JpaBookingRepository;
import ru.practicum.exception.exceptions.BookingNotFoundException;
import ru.practicum.exception.exceptions.DataNotFoundException;
import ru.practicum.exception.exceptions.ForbiddenUpdateException;
import ru.practicum.item.Comment;
import ru.practicum.item.CommentMapper;
import ru.practicum.item.Item;
import ru.practicum.item.ItemMapper;
import ru.practicum.item.dto.CommentResearchDto;
import ru.practicum.item.dto.CommentResponseDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.storage.db.JpaCommentRepository;
import ru.practicum.item.storage.db.JpaItemRepository;
import ru.practicum.request.storage.JpaItemRequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;
import ru.practicum.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final JpaItemRepository jpaItemRepository;
    private final JpaCommentRepository jpaCommentRepository;
    private final JpaBookingRepository jpaBookingRepository;
    private final JpaItemRequestRepository jpaItemRequestRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final UserService userService;

    @Override
    @Transactional
    public ItemDto saveItem(ItemDto request, Integer userId) {
        userService.getData(userId);
        Item item = itemMapper.toItem(request);
        item.setOwner(userMapper.toUser(userService.getData(userId)));
        if (request.getRequestId() != null) {
            if (jpaItemRequestRepository.findById(request.getRequestId()).isEmpty()) {
                log.warn("Request with id = " + request.getRequestId() + " not found");
                throw new DataNotFoundException("Request with id = " + request.getRequestId() + " not found");
            }
            item.setRequest(jpaItemRequestRepository.findById(request.getRequestId()).get());
        }
        return itemMapper.toDto(jpaItemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto request, Integer userId, Integer itemId) {
        Optional<Item> itemOptional = jpaItemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            log.warn("Item with id = " + itemId + " not found");
            throw new DataNotFoundException("Item with id = " + itemId + " not found");
        }
        Item item = itemOptional.get();

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            log.warn("User with id = " + userId + " does not have rights to change item " +
                    "because he is not its owner");
            throw new ForbiddenUpdateException("User with id = " + userId + " does not have rights to change item " +
                    "because he is not its owner");
        }
        Item itemRequest = itemMapper.toItem(request);
        if (itemRequest.getName() == null) {
            itemRequest.setName(item.getName());
        }
        if (itemRequest.getDescription() == null) {
            itemRequest.setDescription(item.getDescription());
        }
        if (itemRequest.getAvailable() == null) {
            itemRequest.setAvailable(item.getAvailable());
        }
        itemRequest.setId(itemId);
        itemRequest.setOwner(item.getOwner());
        return itemMapper.toDto(jpaItemRepository.save(itemRequest));
    }

    @Override
    public ItemDto getItem(Integer id, Integer userId) {
        User user = userMapper.toUser(userService.getData(userId));
        Optional<Item> item = jpaItemRepository.findById(id);
        if (item.isEmpty()) {
            log.warn("Item with id = " + id + " not found");
            throw new DataNotFoundException("Item with id = " + id + " not found");
        }
        ItemDto itemResponse = itemMapper.toDto(item.get());
        BookingResponseToItemDto nextBooking = null;
        BookingResponseToItemDto lastBooking = null;
        if (item.get().getOwner().getId().longValue() == user.getId()) {
            if (!jpaBookingRepository.findBookingByItemAndStartAfter(id, BookingStatus.APPROVED).isEmpty()) {
                nextBooking = bookingMapper.toBookingToItem(jpaBookingRepository
                        .findBookingByItemAndStartAfter(id, BookingStatus.APPROVED).get(0));
            }
            if (!jpaBookingRepository.findBookingByItemAndStartBefore(id, BookingStatus.APPROVED).isEmpty()) {
                lastBooking = bookingMapper.toBookingToItem(jpaBookingRepository
                        .findBookingByItemAndStartBefore(id, BookingStatus.APPROVED).get(0));
            }
        }
        itemResponse.setNextBooking(nextBooking);
        itemResponse.setLastBooking(lastBooking);
        if (!jpaCommentRepository.findCommentByItem_Id(id).isEmpty()) {
            itemResponse.setComments(commentMapper.toListCommentDto(jpaCommentRepository
                    .findCommentByItem_Id(id)));
        } else {
            itemResponse.setComments(new ArrayList<>());
        }
        return itemResponse;
    }

    @Override
    public List<ItemDto> getItemToUser(Integer userId) {
        userService.getData(userId);
        return itemMapper.toDtoList(jpaItemRepository
                        .findByOwnerIdOrderByIdAsc(userId)).stream()
                .peek(
                        i -> {
                            if (!jpaBookingRepository.findBookingByItemAndStartAfter(i.getId(), BookingStatus.APPROVED)
                                    .isEmpty()) {
                                i.setNextBooking(bookingMapper.toBookingToItem(jpaBookingRepository
                                        .findBookingByItemAndStartAfter(i.getId(), BookingStatus.APPROVED).get(0)));
                            }
                            if (!jpaBookingRepository.findBookingByItemAndStartBefore(i.getId(), BookingStatus.APPROVED)
                                    .isEmpty()) {
                                i.setLastBooking(bookingMapper.toBookingToItem(jpaBookingRepository
                                        .findBookingByItemAndStartBefore(i.getId(), BookingStatus.APPROVED).get(0)));
                            }
                            if (!jpaCommentRepository.findCommentByItem_Id(i.getId()).isEmpty()) {
                                i.setComments(commentMapper.toListCommentDto(jpaCommentRepository
                                        .findCommentByItem_Id(i.getId())));
                            }
                        }
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String search, Integer userId) {
        userService.getData(userId);
        List<ItemDto> items = new ArrayList<>();
        if (!search.isBlank()) {
            items = itemMapper.toDtoList(jpaItemRepository.findByNameAndDescription(search, search));
        }
        return items;
    }

    @Override
    @Transactional
    public CommentResponseDto saveComment(Integer itemId, Integer userId, CommentResearchDto research) {
        User user = userMapper.toUser(userService.getData(userId));
        Optional<Item> item = jpaItemRepository.findById(itemId);
        if (item.isEmpty()) {
            log.warn("Item with id = " + itemId + " not found");
            throw new DataNotFoundException("Item with id = " + itemId + " not found");
        }
        if (jpaBookingRepository.findBookingByItem_IdAndBooker_IdAndEndBeforeAndStatus(itemId, userId,
                research.getCreated(), BookingStatus.APPROVED).isEmpty()) {
            log.warn("Completed bookings for a user with id = " +
                    userId + " for item with id = " + itemId + " not found");
            throw new BookingNotFoundException("Completed bookings for a user with id = " +
                    userId + " for item with id = " + itemId + " not found");
        }
        Comment comment = commentMapper.toComment(research);
        comment.setAuthor(user);
        comment.setItem(item.get());
        return commentMapper.toDto(jpaCommentRepository.save(comment));
    }

    @Override
    public Item getItemToBooking(Integer id) {
        Optional<Item> item = jpaItemRepository.findById(id);
        if (item.isEmpty()) {
            log.warn("Item with id = " + id + " not found");
            throw new DataNotFoundException("Item with id = " + id + " not found");
        }
        return item.get();
    }
}
