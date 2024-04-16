package ru.practicum.event.model;

public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static EventState checkState(String stateParam) {
        EventState[] values = EventState.values();

        for (EventState v : values) {
            if (stateParam.toUpperCase().equals(v.name())) {
                return v;
            }
        }
        throw new IllegalStateException("Unknown state: " + stateParam);
    }
}
