package fetch;

public record EventId(EventType type, int number) {

    public String getResourcesRootDirectoryName() {

        return switch (type) {
            case MAIN_EVENT -> "events/" + number;
            case STORY -> "stories/" + String.format("%02d", number);
        };

    }

}
