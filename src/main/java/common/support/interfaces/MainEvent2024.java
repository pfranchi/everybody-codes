package common.support.interfaces;

public interface MainEvent2024 extends MainEvent {

    @Override
    default int getEventYear() {
        return 2024;
    }
}
