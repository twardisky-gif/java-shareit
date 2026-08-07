package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                          LocalDateTime start,
                                                                          LocalDateTime end);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findByItemIdAndStatusOrderByStart(Long itemId, BookingStatus status);

    List<Booking> findByItemIdInAndStatusOrderByStart(Collection<Long> itemIds, BookingStatus status);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(Long bookerId,
                                                           Long itemId,
                                                           BookingStatus status,
                                                           LocalDateTime end);

    boolean existsByItemIdAndStatusAndStartLessThanAndEndGreaterThan(Long itemId,
                                                                     BookingStatus status,
                                                                     LocalDateTime end,
                                                                     LocalDateTime start);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    Optional<Booking> findDetailedById(Long bookingId);

    default Booking requireById(Long bookingId) {
        return findDetailedById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
    }
}
