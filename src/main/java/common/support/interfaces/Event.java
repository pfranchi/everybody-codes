package common.support.interfaces;

import fetch.EventId;
import fetch.EventType;

public interface Event {

    EventType getEventType();

    EventId getEventId();

}
