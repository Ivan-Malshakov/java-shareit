package ru.practicum.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaBookingRepository extends JpaRepository<Booking, Integer> {

    @Query("select b from Booking as b where b.booker.id = :bookerId and b.status = :status order by b.start desc")
    List<Booking> findBookingByBooker_IdAndStatus(Integer bookerId, BookingStatus status);

    List<Booking> findBookingByBooker_IdOrderByStartDesc(Integer bookerId);

    @Query("select b from Booking as b where b.item.owner.id = :ownerId and b.status = :status order by b.start desc")
    List<Booking> findBookingByItem_Owner_IdAndStatus(Integer ownerId, BookingStatus status);

    List<Booking> findBookingByItem_Owner_IdOrderByStartDesc(Integer ownerId);

    @Query("select b from Booking as b where b.item.id = :itemId and b.start > CURRENT_TIMESTAMP " +
            "and b.status = :status order by b.start asc")
    List<Booking> findBookingByItemAndStartAfter(Integer itemId, BookingStatus status);

    @Query("select b from Booking as b where b.item.id = :itemId and b.start < CURRENT_TIMESTAMP " +
            "and b.status = :status order by b.start desc")
    List<Booking> findBookingByItemAndStartBefore(Integer itemId, BookingStatus status);

    List<Booking> findBookingByItem_IdAndBooker_IdAndEndBeforeAndStatus(Integer itemId, Integer bookerId,
                                                                        LocalDateTime end, BookingStatus status);
}
