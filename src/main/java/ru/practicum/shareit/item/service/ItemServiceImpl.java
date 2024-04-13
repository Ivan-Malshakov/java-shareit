package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingResponseToItemDto;
import ru.practicum.shareit.booking.storage.JpaBookingRepository;
import ru.practicum.shareit.exception.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exception.exceptions.DataNotFoundException;
import ru.practicum.shareit.exception.exceptions.ForbiddenUpdateException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentResearchDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.db.JpaCommentRepository;
import ru.practicum.shareit.item.storage.db.JpaItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

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
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final UserService userService;

    @Override
    @Transactional
    public ItemDto saveItem(ItemDto request, Integer userId) {
        userService.getData(userId);
        Item item = itemMapper.toItem(request);
        item.setOwner(userService.getData(userId));
        return itemMapper.toDto(jpaItemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto request, Integer userId, Integer itemId) {
        Item item = jpaItemRepository.findById(itemId).get();
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
        User user = userService.getData(userId);
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
            items = itemMapper.toDtoList(jpaItemRepository.findByName(search, search));
        }
        return items;
    }

    @Override
    @Transactional
    public CommentResponseDto saveComment(Integer itemId, Integer userId, CommentResearchDto research) {
        User user = userService.getData(userId);
        Optional<Item> item = jpaItemRepository.findById(itemId);
        if (item.isEmpty()) {
            log.warn("Item with = " + itemId + " not found");
            throw new DataNotFoundException("Item with = " + itemId + " not found");
        }
        if (jpaBookingRepository.findBookingByItem_IdAndBooker_IdAndEndBeforeAndStatus(itemId, userId,
                research.getCreated(), BookingStatus.APPROVED).isEmpty()) {
            log.warn("Completed bookings for a user with id = " +
                    userId + " for item with id = " + itemId + "not found");
            throw new BookingNotFoundException("Completed bookings for a user with id = " +
                    userId + " for item with id = " + itemId + "not found");
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
            log.warn("Item with = " + id + " not found");
            throw new DataNotFoundException("Item with = " + id + " not found");
        }
        return item.get();
    }
}
