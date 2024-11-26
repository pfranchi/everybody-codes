package challenges.interfaces;

public interface Quest07 extends Quest {

    @Override
    default int getQuestNumber() {
        return 7;
    }
}
