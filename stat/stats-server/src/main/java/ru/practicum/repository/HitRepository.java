package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Hit;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query(value = "SELECT COUNT(DISTINCT ip_address) FROM hits WHERE endpoint_id = :endpointId", nativeQuery = true)
    Long countHitsWithDistinctIpAddresses(@Param("endpointId") Long endpointId);

    @Query(value = "SELECT COUNT(ip_address) FROM hits WHERE endpoint_id = :endpointId", nativeQuery = true)
    Long countHits(@Param("endpointId") Long endpointId);

}