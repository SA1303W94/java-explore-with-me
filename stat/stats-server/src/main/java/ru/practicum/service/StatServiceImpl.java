package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.model.*;
import ru.practicum.repository.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final HitMapper hitMapper;

    private final EndpointMapper endpointMapper;

    private final StatRepository statRepository;

    private final HitRepository hitRepository;

    @Override
    public void create(HitDto hitDto) {
        Endpoint endpoint = statRepository.findByUriEquals(hitDto.getUri());

        if (endpoint == null) {
            endpoint = statRepository.save(endpointMapper.toEntity(hitDto));
        }
        Hit hit = hitMapper.toEntity(hitDto, endpoint);

        hitRepository.save(hit);
        log.info("new hit for URI = {} has been created. app = {}, sentDttm = {}",
                endpoint.getUri(), endpoint.getAppName(), hit.getSentDttm());
    }

    @Override
    public List<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("getting stats by start = {}, end = {}, URIs = {}, unique = {}", start, end, uris, unique);
        List<Endpoint> endpoints = statRepository.findBySentDttmRange(start, end);

        if (uris != null && !uris.isEmpty()) {
            endpoints = endpoints.stream().filter(e -> uris.contains(e.getUri())).collect(Collectors.toList());
            log.info("found {} endpoints by URIs {}", endpoints.size(), uris);
        }
        return endpoints.stream().map(e -> {
            Long hitsCount;

            if (unique) {
                hitsCount = hitRepository.countHitsWithDistinctIpAddresses(e.getId());
            } else {
                hitsCount = hitRepository.countHits(e.getId());
            }
            log.info("found {} hits by URI = {}", hitsCount, e.getUri());
            return endpointMapper.toDto(e, hitsCount);
        }).sorted(Comparator.comparing(StatDto::getHits).reversed()).collect(Collectors.toList());
    }
}