package ru.practicum.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestStatusDto {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}