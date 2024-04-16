package ru.practicum.location.dto;

import javax.validation.constraints.Min;
import lombok.Data;

@Data
public class LocationDto {
    @Min(0)
    private float lat;
    @Min(0)
    private float lon;
}
