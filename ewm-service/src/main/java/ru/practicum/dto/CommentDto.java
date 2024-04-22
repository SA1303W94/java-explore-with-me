package ru.practicum.dto;

import lombok.*;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.groups.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private EventShortDto event;
    private UserDto author;

    @NotBlank(groups = {Create.class})
    @Size(min = 10, max = 512, groups = {Create.class})
    private String text;

    private String state;
    private String createdOn;
    private String updatedOn;
    private String publishedOn;
}