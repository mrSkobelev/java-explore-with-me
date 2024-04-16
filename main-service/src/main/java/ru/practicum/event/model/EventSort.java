package ru.practicum.event.model;

public enum EventSort {
    EVENT_DATE, VIEWS;

    public static EventSort checkSort(String stateParam) {
        EventSort[] values = EventSort.values();

        for (EventSort v : values) {
            if (stateParam.toUpperCase().equals(v.name())) {
                return v;
            }
        }
        throw new IllegalStateException("Unknown sort: " + stateParam);
    }
}
