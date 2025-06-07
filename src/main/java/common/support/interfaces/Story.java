package common.support.interfaces;

import fetch.EventId;
import fetch.EventType;

public interface Story extends Event {

    @Override
    default EventType getEventType() {
        return EventType.STORY;
    }

    int getStoryNumber();

    @Override
    default EventId getEventId() {
        return new EventId(getEventType(), getStoryNumber());
    }
}
