package common.support.interfaces;

import automation.EventId;
import automation.EventType;

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
