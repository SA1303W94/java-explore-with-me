//package ru.practicum.mapper;
//
//import lombok.experimental.UtilityClass;
//import ru.practicum.dto.HitDto;
//
//import java.time.LocalDateTime;
//
//@UtilityClass
//public class HitRequestMapper {
//
//    private static final String APP = "ewm-service";
//
//    public HitDto mapToDto(String uri, String ip) {
//        return HitDto.builder()
//                .app(APP)
//                .uri(uri)
//                .ip(ip)
//                .timestamp(LocalDateTime.now())
//                .build();
//    }
//}