package automation;

public record PuzzleId(EventId eventId, int questNumber, int part) {

    public String getResourcesRootDirectoryName() {
        return eventId().getResourcesRootDirectoryName();
    }

}
