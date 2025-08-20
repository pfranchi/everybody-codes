package common.support.interfaces;

public interface Quest05 extends Quest {

    @Override
    default int getQuestNumber() {
        return 5;
    }

}
