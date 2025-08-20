package common.support.interfaces;

public interface Quest02 extends Quest {

    @Override
    default int getQuestNumber() {
        return 2;
    }

}
