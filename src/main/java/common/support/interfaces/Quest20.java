package common.support.interfaces;

public interface Quest20 extends Quest {

    @Override
    default int getQuestNumber() {
        return 20;
    }
}
