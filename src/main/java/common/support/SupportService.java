package common.support;

import automation.EventId;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SupportService {

    public static Path initializeQuestDetail(EventId eventId, int questNumber) {

        String resourcesRootDirName = eventId.getResourcesRootDirectoryName();
        Path folder = Path.of("src/main/resources/inputs/" + resourcesRootDirName);
        if (!Files.exists(folder)) {

            try {
                Files.createDirectory(folder);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        Path path = Path.of("src/main/resources/inputs/" + resourcesRootDirName + "/quest" + String.format("%02d", questNumber) + ".json");

        if (!Files.exists(path)) {

            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        String fileContent = """
                        {
                            "challengeId": {
                                "eventNumber": %1$s,
                                "questNumber": %2$s
                            },
                            "parts": [
                                {
                                    "partNumber": 1,
                                    "encryptedObtained": false
                                }, {
                                    "partNumber": 2,
                                    "encryptedObtained": false
                                }, {
                                    "partNumber": 3,
                                    "encryptedObtained": false
                                }
                            ],
                            "answerAttempts": {
                                "part1": [],
                                "part2": [],
                                "part3": []
                            }
                        }
                        """.formatted(eventId.number(), questNumber);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(fileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return path;

    }

}
