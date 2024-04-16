package ru.practicum.event.dto;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.event.model.EventSort;

@Data
public class PublicEventQueryParams {
    private String text;

    private List<Long> categories;

    private Boolean paid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

    private Boolean onlyAvailable;

    private EventSort sort;

    @Min(0)
    private int from = 0;

    @Min(1)
    private int size = 10;
}
