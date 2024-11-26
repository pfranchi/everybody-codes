package fetch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public final class Inputs {

    private Inputs() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    private static final Map<PuzzleId, String> inputCache = new HashMap<>();
    private static final HttpResponse.BodyHandler<String> responseBodyHandler = HttpResponse.BodyHandlers.ofString();
    private static final HttpClient client = HttpClient.newHttpClient();

    public static String fetch(PuzzleId puzzleId) throws PuzzleNotAvailableException {

        // Fetch from the local cache
        if (inputCache.containsKey(puzzleId)) {
            return inputCache.get(puzzleId);
        }

        // If the date is in the future, the puzzle is not available
        int year = puzzleId.ecEvent();
        int day = puzzleId.questNumber();

        LocalDate target = LocalDate.of(year, Month.NOVEMBER, day);
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(target.atStartOfDay())) {
            throw new PuzzleNotAvailableException();
        }

        // Fetch and put in local cache
        String input;
        try {
            input = fetchNotInLocalCache(puzzleId);
            inputCache.put(puzzleId, input);
            return input;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }

    }

    private static String fetchNotInLocalCache(PuzzleId puzzleId) throws IOException, InterruptedException {

        Path filePath = Path.of("src/main/resources/inputs/" + puzzleId.ecEvent() + "/quest"
                + String.format("%02d", puzzleId.questNumber()) + "/part" + puzzleId.part() + ".txt");
        Path folderPath = filePath.getParent();

        if (Files.notExists(folderPath)) {
            Files.createDirectories(folderPath);
        }

        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }

        String fileContent = new String(Files.readAllBytes(filePath));

        if (fileContent.isEmpty()) {

            // Fetch from the site and write to the file
            String inputFromSite = fetchFromSite(puzzleId);

            try (BufferedWriter bw = Files.newBufferedWriter(filePath)) {
                bw.write(inputFromSite);
            }

            return inputFromSite;

        }

        return fileContent;

    }

    private static String fetchFromSite(PuzzleId puzzleId) {
        throw new UnsupportedOperationException("Fetch from site not implemented yet");
    }

}
