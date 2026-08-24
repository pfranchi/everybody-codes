package common.support.interfaces;

public interface Story04 extends Story {

    @Override
    default int getStoryNumber() {
        return 4;
    }

}

