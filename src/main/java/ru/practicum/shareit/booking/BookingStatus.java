package ru.practicum.shareit.booking;

public enum BookingStatus {
    WAITING("WAITING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    CANCELED("CANCELED"),
    ;
    private final String status;

    BookingStatus(String status) {
        this.status = status;
    }

    public String getBookingStatus() {
        return status;
    }
}
