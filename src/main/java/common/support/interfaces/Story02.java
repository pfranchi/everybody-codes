package common.support.interfaces;

public interface Story02 extends Story {

    @Override
    default int getStoryNumber() {
        return 2;
    }
}
