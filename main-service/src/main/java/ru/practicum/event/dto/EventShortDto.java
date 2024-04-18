package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import lombok.Data;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

@Data
public class EventShortDto {
    private Long id;
    private String title;
    private String description;
    private String annotation;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private CategoryDto category;
    private Long confirmedRequests;
    private UserShortDto initiator;
    private Boolean paid;
    private Long views;
}
