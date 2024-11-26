package challenges.interfaces;

public interface Quest09 extends Quest {

    @Override
    default int getQuestNumber() {
        return 9;
    }
}
