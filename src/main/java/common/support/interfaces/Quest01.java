package common.support.interfaces;

public interface Quest01 extends Quest {

    @Override
    default int getQuestNumber() {
        return 1;
    }
}
