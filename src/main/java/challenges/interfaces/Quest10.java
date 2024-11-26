package challenges.interfaces;

public interface Quest10 extends Quest {

    @Override
    default int getQuestNumber() {
        return 10;
    }
}
