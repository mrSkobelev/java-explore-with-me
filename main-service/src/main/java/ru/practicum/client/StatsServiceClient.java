package ru.practicum.client;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.model.Event;

public interface StatsServiceClient {

    void saveStats(HttpServletRequest request);

    List<ViewStatsDto> getViews(List<Event> events);
}
