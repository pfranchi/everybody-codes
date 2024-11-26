package challenges.interfaces;

public interface Quest03 extends Quest {

    @Override
    default int getQuestNumber() {
        return 3;
    }
}
