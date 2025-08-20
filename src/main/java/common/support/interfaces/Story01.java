package common.support.interfaces;

public interface Story01 extends Story {

    @Override
    default int getStoryNumber() {
        return 1;
    }

}
