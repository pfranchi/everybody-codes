package common.support.interfaces;

import automation.EventId;
import automation.EventType;

public interface MainEvent extends Event {

    @Override
    default EventType getEventType() {
        return EventType.MAIN_EVENT;
    }

    int getEventYear();

    @Override
    default EventId getEventId() {
        return new EventId(getEventType(), getEventYear());
    }

}
