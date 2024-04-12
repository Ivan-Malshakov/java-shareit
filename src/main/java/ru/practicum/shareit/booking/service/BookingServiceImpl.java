package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingResearchDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.storage.JpaBookingRepository;
import ru.practicum.shareit.exception.exceptions.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final JpaBookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponseDto saveBooking(BookingResearchDto bookingDto, Integer userId) {
        User booker = userService.getData(userId);
        Item item = itemService.getItemToBooking(bookingDto.getItemId());
        Integer itemId = item.getId();
        Integer bookerId = booker.getId();

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())
                || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            log.warn("The end date of booking is earlier or equal than to the start date");
            throw new BookingDateException("The end date of booking is earlier or equal than to the start date");
        }
        if (!item.getAvailable()) {
            log.warn("Item with id = " + itemId + " unavailable");
            throw new UnavailableItemException("Item with id = " + itemId + " unavailable");
        }
        if (item.getOwner().getId().longValue() == bookerId) {
            log.warn("User with id = " + bookerId + " is the owner of item " +
                    "with id = " + itemId);
            throw new ForbiddenAccessException("User with id = " + bookerId + " is the owner of item " +
                    "with id = " + itemId);
        }
        Booking research = bookingMapper.toBooking(bookingDto);
        research.setStatus(BookingStatus.WAITING);
        research.setItem(item);
        research.setBooker(booker);
        return bookingMapper.toDto(bookingRepository.save(research));
    }

    @Override
    @Transactional
    public BookingResponseDto approvedOrRejectBooking(Integer userId, Integer bookingId, Boolean approved) {
        User owner = userService.getData(userId);
        Integer ownerId = owner.getId();
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            log.warn("Booking with id = " + bookingId + " not found");
            throw new DataNotFoundException("Booking with id = " + bookingId + " not found");
        }
        if (booking.get().getItem().getOwner().getId().longValue() != ownerId) {
            log.warn("User with id = " + ownerId + " is not the owner of item" +
                    " с id = " + booking.get().getItem().getId());
            throw new ForbiddenAccessException("User with id = " + ownerId + " is not the owner of item" +
                    " с id = " + booking.get().getItem().getId());
        }
        if (booking.get().getStatus() != BookingStatus.WAITING) {
            log.warn("Not access to status change");
            throw new ForbiddenAccessChangeStatusException("Not access to status change");
        }
        if (approved) {
            booking.get().setStatus(BookingStatus.APPROVED);
        } else {
            booking.get().setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toDto(bookingRepository.save(booking.get()));
    }

    @Override
    public BookingResponseDto getBooking(Integer userId, Integer bookingId) {
        User user = userService.getData(userId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            log.warn("Booking with id = " + bookingId + " not found");
            throw new DataNotFoundException("Booking with id = " + bookingId + " not found");
        }
        if (booking.get().getItem().getOwner().getId().longValue() != user.getId()
                && user.getId().longValue() != booking.get().getBooker().getId()) {
            log.warn("User with id = " + user.getId() + " is not the owner of item" +
                    " with id = " + booking.get().getItem().getId() + " or not the owner of booking with id = "
                    + booking.get().getId());
            throw new ForbiddenAccessException("User with id = " + user.getId() + " is not the owner of item" +
                    " with id = " + booking.get().getItem().getId() + " or not the owner of booking with id = "
                    + booking.get().getId());
        }
        return bookingMapper.toDto(booking.get());
    }

    @Override
    public List<BookingResponseDto> getBookingToUser(Integer userId, String state) {
        userService.getData(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ("ALL"):
                bookings = bookingRepository.findBookingByBooker_IdOrderByStartDesc(userId);
                break;
            case ("CURRENT"):
                bookings = bookingRepository.findBookingByBooker_IdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now())
                                && b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case ("PAST"):
                bookings = bookingRepository.findBookingByBooker_IdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case ("FUTURE"):
                bookings = bookingRepository.findBookingByBooker_IdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case ("WAITING"):
                bookings = bookingRepository.findBookingByBooker_IdAndStatus(userId, BookingStatus.WAITING);
                break;
            case ("REJECTED"):
                bookings = bookingRepository.findBookingByBooker_IdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                log.warn("Unknown state: " + state);
                throw new StateNotFoundException("Unknown state: " + state);
        }
        return bookingMapper.toListDto(bookings);
    }

    @Override
    public List<BookingResponseDto> getBookingToOwner(Integer userId, String state) {
        userService.getData(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ("ALL"):
                bookings = bookingRepository.findBookingByItem_Owner_IdOrderByStartDesc(userId);
                break;
            case ("CURRENT"):
                bookings = bookingRepository.findBookingByItem_Owner_IdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getStart().isBefore(LocalDateTime.now())
                                && b.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case ("PAST"):
                bookings = bookingRepository.findBookingByItem_Owner_IdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case ("FUTURE"):
                bookings = bookingRepository.findBookingByItem_Owner_IdOrderByStartDesc(userId).stream()
                        .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case ("WAITING"):
                bookings = bookingRepository.findBookingByItem_Owner_IdAndStatus(userId, BookingStatus.WAITING);
                break;
            case ("REJECTED"):
                bookings = bookingRepository.findBookingByItem_Owner_IdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                log.warn("Unknown state: " + state);
                throw new StateNotFoundException("Unknown state: " + state);
        }
        return bookingMapper.toListDto(bookings);
    }
}
