package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Endpoint;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Endpoint, Long> {

    Endpoint findByUriEquals(String uri);

    @Query(value = "SELECT e.endpoint_id, e.app_name, e.uri FROM endpoints e INNER JOIN hits h using(endpoint_id) "
            + "WHERE h.sent_dttm >= :start AND h.sent_dttm <= :end "
            + "GROUP BY e.endpoint_id", nativeQuery = true)
    List<Endpoint> findBySentDttmRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}