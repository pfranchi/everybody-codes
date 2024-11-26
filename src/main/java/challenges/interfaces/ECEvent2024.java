package challenges.interfaces;

public interface ECEvent2024 extends ECEvent {

    @Override
    default int getEventYear() {
        return 2024;
    }
}
