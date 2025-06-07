package common.support.interfaces;

public interface Quest04 extends Quest {

    @Override
    default int getQuestNumber() {
        return 4;
    }
}
