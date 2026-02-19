package common.support.interfaces;

public interface Story03 extends Story {

    @Override
    default int getStoryNumber() {
        return 3;
    }

}

