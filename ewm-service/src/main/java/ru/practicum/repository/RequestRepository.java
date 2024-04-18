package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.type.RequestStatus;
import ru.practicum.model.Event;
import ru.practicum.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request findByRequesterIdAndEventId(Long userId, Long eventId);

    List<Optional<Request>> findByRequesterId(Long userId);

    Integer countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Optional<Request>> findByEventIn(List<Event> userEvents);

    List<Request> findByEventInAndStatus(List<Event> userEvents, RequestStatus status);

    List<Request> findByEventIdAndStatus(Long eventId, RequestStatus status);

    @Modifying
    @Query("update Request r set r.status = :newStatus where (r.event = :event and r.status = :searchStatus)")
    void updateRequestStatusByEventIdAndStatus(@Param(value = "event") Event event,
                                               @Param(value = "searchStatus") RequestStatus searchStatus,
                                               @Param(value = "newStatus") RequestStatus newStatus);

}