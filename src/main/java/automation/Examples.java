package automation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public final class Examples {

    private Examples() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static List<Example> fetchExamples(PuzzleId puzzleId) {

        String formattedQuestNumber = String.format("%02d", puzzleId.questNumber());
        Path folderPath = Path.of(
                "src/main/resources/examples/" + puzzleId.getResourcesRootDirectoryName()
                        + "/quest" + formattedQuestNumber + "/part" + puzzleId.part()
        );

        if (Files.notExists(folderPath)) {
            return Collections.emptyList();
        }

        try (Stream<Path> paths = Files.list(folderPath)) {

            List<Example> examples = new ArrayList<>();
            for (Path path : paths.toList()) {

                Example example = new Example(path, Files.readString(path));

                examples.add(example);
            }

            return examples;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();

    }

}
