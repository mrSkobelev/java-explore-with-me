package ru.practicum.location.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.location.model.Location;

@UtilityClass
public class LocationMapper {
    public LocationDto toLocationDto(Location location) {
        LocationDto locationDto = new LocationDto();

        locationDto.setLat(location.getLat());
        locationDto.setLon(location.getLon());

        return locationDto;
    }
}
