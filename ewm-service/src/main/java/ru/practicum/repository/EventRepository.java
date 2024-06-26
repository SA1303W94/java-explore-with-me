package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.type.PublicationState;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByInitiatorId(Long userId, Pageable pageable);

    List<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    List<Event> findByIdIn(List<Long> eventIds);

    Event findFirstByCategoryId(Long catId);

    @Query(value = "SELECT * FROM Events e WHERE (:userId is null or e.initiator_id IN (cast(cast(:userId AS TEXT) AS BIGINT))) "
            + "and (:publication_states is null or e.publication_state IN (cast(:publication_states AS text))) "
            + "and (:categories is null or e.category_id IN (cast(cast(:categories AS TEXT) AS BIGINT))) "
            + "and (cast(:rangeStart AS timestamp) is null or e.event_dttm >= cast(:rangeStart AS timestamp))"
            + "and (cast(:rangeEnd AS timestamp) is null or e.event_dttm < cast(:rangeEnd AS timestamp))",
            nativeQuery = true)
    List<Event> findEvents(@Param("userId") List<Long> userId,
                           @Param("publication_states") List<String> states,
                           @Param("categories") List<Long> categories,
                           @Param("rangeStart") LocalDateTime rangeStart,
                           @Param("rangeEnd") LocalDateTime rangeEnd,
                           Pageable pageable);

    @Query(value = "SELECT * FROM Events e WHERE (e.publication_state = 'PUBLISHED') "
            + "and (:text is null or lower(e.annotation) LIKE lower(concat('%',cast(:text AS text),'%')) "
            + "or lower(e.description) LIKE lower(concat('%',cast(:text AS text),'%'))) "
            + "and (:categories is null or e.category_id IN (cast(cast(:categories AS TEXT) AS BIGINT))) "
            + "and (:paid is null or e.paid = cast(cast(:paid AS text) AS BOOLEAN)) "
            + "and (e.event_dttm >= :rangeStart) "
            + "and (cast(:rangeEnd AS timestamp) is null or e.event_dttm < cast(:rangeEnd AS timestamp))",
            nativeQuery = true)
    List<Event> findPublishedEvents(@Param("text") String text,
                                    @Param("categories") List<Long> categories,
                                    @Param("paid") Boolean paid,
                                    @Param("rangeStart") LocalDateTime rangeStart,
                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                    Pageable pageable);

    Optional<Event> findByIdAndPublicationState(Long eventId, PublicationState publicationState);
}