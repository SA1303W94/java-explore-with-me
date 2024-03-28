package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.model.Endpoint;
import ru.practicum.model.EndpointMapper;
import ru.practicum.model.Hit;
import ru.practicum.model.HitMapper;
import ru.practicum.repository.HitRepository;
import ru.practicum.repository.StatRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.exception.ExceptionType.START_DATE_IN_PAST;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;

    private final HitRepository hitRepository;

    @Override
    public void create(HitDto hitDto) {
        Endpoint endpoint = statRepository.findByUriEquals(hitDto.getUri());

        if (endpoint == null) {
            endpoint = statRepository.save(EndpointMapper.toEntity(hitDto));
        }
        Hit hit = HitMapper.toEntity(hitDto, endpoint);

        hitRepository.save(hit);
        log.info("new hit for URI = {} has been created. app = {}, sentDttm = {}",
                endpoint.getUri(), endpoint.getAppName(), hit.getSentDttm());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("getting stats by start = {}, end = {}, URIs = {}, unique = {}", start, end, uris, unique);
        if (start.isAfter(end)) {
            throw new ValidationException((String.format(START_DATE_IN_PAST.getValue(), start)));
        }
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
            return EndpointMapper.toDto(e, hitsCount);
        }).sorted(Comparator.comparing(StatDto::getHits).reversed()).collect(Collectors.toList());
    }
}