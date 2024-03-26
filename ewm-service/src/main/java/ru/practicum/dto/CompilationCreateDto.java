package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompilationCreateDto {

    private List<Long> events;

    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean pinned = false;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}