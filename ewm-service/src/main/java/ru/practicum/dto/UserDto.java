package ru.practicum.dto;

import lombok.*;
import ru.practicum.groups.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(groups = {Create.class})
    @Size(min = 2, max = 250, groups = {Create.class})
    private String name;

    @NotBlank(groups = {Create.class})
    @Email(groups = {Create.class})
    @Size(min = 6, max = 254, groups = {Create.class})
    private String email;

    private List<Long> subscriptionUserIds;
}