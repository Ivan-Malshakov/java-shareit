package ru.practicum.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.booking.storage.JpaBookingRepository;
import ru.practicum.item.Item;
import ru.practicum.item.storage.db.JpaItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.storage.db.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class JpaBookingRepositoryTest {
    @Autowired
    private JpaBookingRepository bookingRepository;
    @Autowired
    private JpaUserRepository userRepository;
    @Autowired
    private JpaItemRepository itemRepository;

    @Test
    @DirtiesContext
    void findBookingByBooker_IdAndStatusWithTwoRequestShouldBeOk() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);
        Item item1 = new Item(null, "Item 1", "Desc 1", true, saveUser1,
                null);
        Item item2 = new Item(null, "Item 2", "Desc 2", true, saveUser1,
                null);
        Item saveItem1 = itemRepository.save(item1);
        Item saveItem2 = itemRepository.save(item2);
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        Booking booking1 = new Booking(null, created1, end1, saveItem1, saveUser2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, created2, end2, saveItem2, saveUser2, BookingStatus.APPROVED);
        Booking booking1Save = bookingRepository.save(booking1);
        Booking booking2Save = bookingRepository.save(booking2);

        List<Booking> bookingActual = bookingRepository.findBookingByBooker_IdAndStatus(saveUser2.getId(),
                BookingStatus.APPROVED);

        assertEquals(2, bookingActual.size());
        assertTrue(bookingActual.contains(booking1Save));
        assertTrue(bookingActual.contains(booking2Save));
    }

    @Test
    @DirtiesContext
    void findBookingByBooker_IdAndStatusWithOneRequestShouldBeOk() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);
        Item item1 = new Item(null, "Item 1", "Desc 1", true, saveUser1,
                null);
        Item item2 = new Item(null, "Item 2", "Desc 2", true, saveUser1,
                null);
        Item saveItem1 = itemRepository.save(item1);
        Item saveItem2 = itemRepository.save(item2);
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        Booking booking1 = new Booking(null, created1, end1, saveItem1, saveUser2, BookingStatus.REJECTED);
        Booking booking2 = new Booking(null, created2, end2, saveItem2, saveUser2, BookingStatus.APPROVED);
        Booking booking1Save = bookingRepository.save(booking1);
        Booking booking2Save = bookingRepository.save(booking2);

        List<Booking> bookingActual = bookingRepository.findBookingByBooker_IdAndStatus(saveUser2.getId(),
                BookingStatus.APPROVED);

        assertEquals(1, bookingActual.size());
        assertTrue(bookingActual.contains(booking2Save));
    }

    @Test
    @DirtiesContext
    void findBookingByBooker_IdOrderByStartDescWithThreeRequestShouldBeOk() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);
        Item item1 = new Item(null, "Item 1", "Desc 1", true, saveUser1,
                null);
        Item item2 = new Item(null, "Item 2", "Desc 2", true, saveUser1,
                null);
        Item saveItem1 = itemRepository.save(item1);
        Item saveItem2 = itemRepository.save(item2);
        LocalDateTime created1 = LocalDateTime.now().plusDays(100);
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime end3 = created2.plusDays(50);
        Booking booking1 = new Booking(null, created1, end1, saveItem1, saveUser2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, created2, end2, saveItem2, saveUser2, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, created3, end3, saveItem1, saveUser2, BookingStatus.APPROVED);
        Booking booking1Save = bookingRepository.save(booking1);
        Booking booking2Save = bookingRepository.save(booking2);
        Booking booking3Save = bookingRepository.save(booking3);

        List<Booking> bookingActual = bookingRepository.findBookingByBooker_IdOrderByStartDesc(saveUser2.getId());

        assertEquals(3, bookingActual.size());
        assertEquals(bookingActual.get(0), booking1Save);
        assertEquals(bookingActual.get(1), booking3Save);
        assertEquals(bookingActual.get(2), booking2Save);
    }

    @Test
    @DirtiesContext
    void findBookingByBooker_IdOrderByStartDescWithTwoRequestShouldBeOk() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);
        Item item1 = new Item(null, "Item 1", "Desc 1", true, saveUser1,
                null);
        Item item2 = new Item(null, "Item2", "Desc 2", true, saveUser1,
                null);
        Item saveItem1 = itemRepository.save(item1);
        Item saveItem2 = itemRepository.save(item2);
        LocalDateTime created1 = LocalDateTime.now().plusDays(100);
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime end3 = created2.plusDays(50);
        Booking booking1 = new Booking(null, created1, end1, saveItem1, saveUser2, BookingStatus.REJECTED);
        Booking booking2 = new Booking(null, created2, end2, saveItem2, saveUser2, BookingStatus.WAITING);
        Booking booking3 = new Booking(null, created3, end3, saveItem1, saveUser2, BookingStatus.APPROVED);
        Booking booking1Save = bookingRepository.save(booking1);
        Booking booking2Save = bookingRepository.save(booking2);
        Booking booking3Save = bookingRepository.save(booking3);

        List<Booking> bookingActual = bookingRepository.findBookingByBooker_IdOrderByStartDesc(saveUser2.getId());

        assertEquals(3, bookingActual.size());
        assertEquals(bookingActual.get(0), booking1Save);
        assertEquals(bookingActual.get(1), booking3Save);
        assertEquals(bookingActual.get(2), booking2Save);
    }

    @Test
    @DirtiesContext
    void findBookingByItem_Owner_IdAndStatusShouldBeOk() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        User user3 = new User(null, "User 3", "user3@yandex.ru");
        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);
        User saveUser3 = userRepository.save(user3);
        Item item1 = new Item(null, "Item 1", "Desc 1", true, saveUser1,
                null);
        Item item2 = new Item(null, "Item 2", "Desc 2", true, saveUser1,
                null);
        Item item3 = new Item(null, "Item 3", "Desc 3", true, saveUser3,
                null);
        Item saveItem1 = itemRepository.save(item1);
        Item saveItem2 = itemRepository.save(item2);
        Item saveItem3 = itemRepository.save(item3);
        LocalDateTime created1 = LocalDateTime.now().plusDays(100);
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime end3 = created2.plusDays(50);
        LocalDateTime created4 = LocalDateTime.now().plusDays(2);
        LocalDateTime end4 = created2.plusDays(2);
        Booking booking1 = new Booking(null, created1, end1, saveItem1, saveUser2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, created2, end2, saveItem2, saveUser2, BookingStatus.WAITING);
        Booking booking3 = new Booking(null, created3, end3, saveItem1, saveUser2, BookingStatus.APPROVED);
        Booking booking4 = new Booking(null, created4, end4, saveItem3, saveUser2, BookingStatus.REJECTED);
        Booking booking1Save = bookingRepository.save(booking1);
        Booking booking2Save = bookingRepository.save(booking2);
        Booking booking3Save = bookingRepository.save(booking3);
        Booking booking4Save = bookingRepository.save(booking4);

        List<Booking> bookingActual = bookingRepository.findBookingByItem_Owner_IdAndStatus(saveItem1.getId(),
                BookingStatus.APPROVED);

        assertEquals(2, bookingActual.size());
        assertEquals(bookingActual.get(0), booking1Save);
        assertEquals(bookingActual.get(1), booking3Save);
    }

    @Test
    @DirtiesContext
    void findBookingByItem_Owner_IdOrderByStartDescShouldBeOk() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        User user3 = new User(null, "User 3", "user3@yandex.ru");
        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);
        User saveUser3 = userRepository.save(user3);
        Item item1 = new Item(null, "Item 1", "Desc 1", true, saveUser1,
                null);
        Item item2 = new Item(null, "Item 2", "Desc 2", true, saveUser1,
                null);
        Item item3 = new Item(null, "Item 3", "Desc 3", true, saveUser3,
                null);
        Item item4 = new Item(null, "Item 4", "Desc 4", true, saveUser1,
                null);
        Item saveItem1 = itemRepository.save(item1);
        Item saveItem2 = itemRepository.save(item2);
        Item saveItem3 = itemRepository.save(item3);
        Item saveItem4 = itemRepository.save(item4);
        LocalDateTime created1 = LocalDateTime.now().plusDays(100);
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime end3 = created2.plusDays(50);
        LocalDateTime created4 = LocalDateTime.now().plusDays(2);
        LocalDateTime end4 = created2.plusDays(2);
        LocalDateTime created5 = LocalDateTime.now().plusDays(75);
        LocalDateTime end5 = created2.plusDays(6);
        Booking booking1 = new Booking(null, created1, end1, saveItem1, saveUser2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, created2, end2, saveItem2, saveUser2, BookingStatus.WAITING);
        Booking booking3 = new Booking(null, created3, end3, saveItem1, saveUser3, BookingStatus.APPROVED);
        Booking booking4 = new Booking(null, created4, end4, saveItem3, saveUser2, BookingStatus.REJECTED);
        Booking booking5 = new Booking(null, created5, end5, saveItem4, saveUser3, BookingStatus.APPROVED);
        Booking booking1Save = bookingRepository.save(booking1);
        Booking booking2Save = bookingRepository.save(booking2);
        Booking booking3Save = bookingRepository.save(booking3);
        Booking booking4Save = bookingRepository.save(booking4);
        Booking booking5Save = bookingRepository.save(booking5);

        List<Booking> bookingActual = bookingRepository.findBookingByItem_Owner_IdOrderByStartDesc(
                saveItem1.getOwner().getId());

        assertEquals(4, bookingActual.size());
        assertEquals(bookingActual.get(0), booking1Save);
        assertEquals(bookingActual.get(1), booking5Save);
        assertEquals(bookingActual.get(2), booking3Save);
        assertEquals(bookingActual.get(3), booking2Save);
    }

    @Test
    void findBookingByItemAndStartAfterShouldBeOk() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        User user3 = new User(null, "User 3", "user3@yandex.ru");
        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);
        User saveUser3 = userRepository.save(user3);
        Item item1 = new Item(null, "Item 1", "Desc 1", true, saveUser1,
                null);
        Item saveItem1 = itemRepository.save(item1);
        LocalDateTime created1 = LocalDateTime.now().plusDays(100);
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime end3 = created2.plusDays(50);
        LocalDateTime created4 = LocalDateTime.now().plusDays(2);
        LocalDateTime end4 = created2.plusDays(2);
        LocalDateTime created5 = LocalDateTime.now().minusDays(1);
        LocalDateTime end5 = created2.plusDays(6);
        Booking booking1 = new Booking(null, created1, end1, saveItem1, saveUser2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, created2, end2, saveItem1, saveUser2, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, created3, end3, saveItem1, saveUser3, BookingStatus.APPROVED);
        Booking booking4 = new Booking(null, created4, end4, saveItem1, saveUser2, BookingStatus.REJECTED);
        Booking booking5 = new Booking(null, created5, end5, saveItem1, saveUser3, BookingStatus.APPROVED);
        Booking booking1Save = bookingRepository.save(booking1);
        Booking booking2Save = bookingRepository.save(booking2);
        Booking booking3Save = bookingRepository.save(booking3);
        Booking booking4Save = bookingRepository.save(booking4);
        Booking booking5Save = bookingRepository.save(booking5);

        List<Booking> bookingActual = bookingRepository.findBookingByItemAndStartAfter(saveItem1.getId(),
                BookingStatus.APPROVED);

        assertEquals(5, bookingRepository.findAll().size());
        assertEquals(3, bookingActual.size());
        assertEquals(bookingActual.get(0), booking2Save);
        assertEquals(bookingActual.get(1), booking3Save);
        assertEquals(bookingActual.get(2), booking1Save);
    }

    @Test
    @DirtiesContext
    void findBookingByItemAndStartBeforeShouldBeOk() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        User user3 = new User(null, "User 3", "user3@yandex.ru");
        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);
        User saveUser3 = userRepository.save(user3);
        Item item1 = new Item(null, "Item 1", "Desc 1", true, saveUser1,
                null);
        Item saveItem1 = itemRepository.save(item1);
        LocalDateTime created1 = LocalDateTime.now().plusDays(100);
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().plusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime end3 = created2.plusDays(50);
        LocalDateTime created4 = LocalDateTime.now().plusDays(2);
        LocalDateTime end4 = created2.plusDays(2);
        LocalDateTime created5 = LocalDateTime.now().minusDays(1);
        LocalDateTime end5 = created2.plusDays(6);
        Booking booking1 = new Booking(null, created1, end1, saveItem1, saveUser2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, created2, end2, saveItem1, saveUser2, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, created3, end3, saveItem1, saveUser3, BookingStatus.APPROVED);
        Booking booking4 = new Booking(null, created4, end4, saveItem1, saveUser2, BookingStatus.REJECTED);
        Booking booking5 = new Booking(null, created5, end5, saveItem1, saveUser3, BookingStatus.APPROVED);
        Booking booking1Save = bookingRepository.save(booking1);
        Booking booking2Save = bookingRepository.save(booking2);
        Booking booking3Save = bookingRepository.save(booking3);
        Booking booking4Save = bookingRepository.save(booking4);
        Booking booking5Save = bookingRepository.save(booking5);

        List<Booking> bookingActual = bookingRepository.findBookingByItemAndStartBefore(saveItem1.getId(),
                BookingStatus.APPROVED);

        assertEquals(5, bookingRepository.findAll().size());
        assertEquals(1, bookingActual.size());
        assertEquals(bookingActual.get(0), booking5Save);
    }

    @Test
    @DirtiesContext
    void findBookingByItem_IdAndBooker_IdAndEndBeforeAndStatusShouldBeOk() {
        User user1 = new User(null, "User 1", "user1@yandex.ru");
        User user2 = new User(null, "User 2", "user2@yandex.ru");
        User user3 = new User(null, "User 3", "user3@yandex.ru");
        User saveUser1 = userRepository.save(user1);
        User saveUser2 = userRepository.save(user2);
        User saveUser3 = userRepository.save(user3);
        Item item1 = new Item(null, "Item 1", "Item 1", true, saveUser1,
                null);
        Item saveItem1 = itemRepository.save(item1);
        LocalDateTime created1 = LocalDateTime.now().minusDays(100);
        LocalDateTime end1 = created1.plusDays(30);
        LocalDateTime created2 = LocalDateTime.now().minusDays(10);
        LocalDateTime end2 = created2.plusDays(50);
        LocalDateTime created3 = LocalDateTime.now().plusDays(50);
        LocalDateTime end3 = created2.plusDays(49);
        LocalDateTime created4 = LocalDateTime.now().minusDays(2);
        LocalDateTime end4 = created2.plusDays(1);
        LocalDateTime created5 = LocalDateTime.now().minusDays(1);
        LocalDateTime end5 = created2.plusDays(6);
        Booking booking1 = new Booking(null, created1, end1, saveItem1, saveUser2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, created2, end2, saveItem1, saveUser2, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, created3, end3, saveItem1, saveUser3, BookingStatus.APPROVED);
        Booking booking4 = new Booking(null, created4, end4, saveItem1, saveUser2, BookingStatus.REJECTED);
        Booking booking5 = new Booking(null, created5, end5, saveItem1, saveUser3, BookingStatus.APPROVED);
        Booking booking1Save = bookingRepository.save(booking1);
        Booking booking2Save = bookingRepository.save(booking2);
        Booking booking3Save = bookingRepository.save(booking3);
        Booking booking4Save = bookingRepository.save(booking4);
        Booking booking5Save = bookingRepository.save(booking5);

        List<Booking> bookingActual = bookingRepository.findBookingByItem_IdAndBooker_IdAndEndBeforeAndStatus(1,
                2, LocalDateTime.now(), BookingStatus.APPROVED);

        assertEquals(5, bookingRepository.findAll().size());
        assertEquals(1, bookingActual.size());
        assertEquals(bookingActual.get(0), booking1Save);
    }
}
