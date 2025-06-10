package common.support.interfaces;

import automation.EventId;
import automation.EventType;

public interface Event {

    EventType getEventType();

    EventId getEventId();

}
