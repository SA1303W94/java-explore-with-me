package ru.practicum.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompilationUpdateDto {

    private List<Long> events;

    private Boolean pinned = false;

    @Size(min = 1, max = 50)
    private String title;
}