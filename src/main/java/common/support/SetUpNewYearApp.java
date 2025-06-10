package common.support;

import automation.EventId;
import automation.EventType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SetUpNewYearApp {

    public static void main(String[] args) {

        EventType type = EventType.MAIN_EVENT;
        int year = 2025;

        EventId eventId = new EventId(type, year);

        try {

            initializeQuestDetails(eventId);

            /*
            createInterface(year);
            createChallenges(year);
            createExamples(year);
            createTests(year);

             */

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void initializeQuestDetails(EventId eventId) throws IOException {

        if (eventId.type() == EventType.MAIN_EVENT) {

            int eventNumber = eventId.number();

            Path folder = Path.of("src/main/resources/inputs/events/" + eventNumber);
            if (!Files.exists(folder)) {
                Files.createDirectory(folder);
            }

            for (int questNumber = 1; questNumber <= 20; questNumber++) {

                Path path = Path.of("src/main/resources/inputs/events/" + eventNumber + "/quest" + String.format("%02d", questNumber) + ".json");

                if (!Files.exists(path)) {
                    Files.createFile(path);
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
                        """.formatted(eventNumber, questNumber);

                try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                    writer.write(fileContent);
                }

            }


        } else {
            throw new UnsupportedOperationException();
        }

    }

    private static void createInterface(int year) throws IOException {

        Path path = Path.of("src/main/java/challenges/interfaces/Year" + year + ".java");
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {

            String fileContent = """
                    package challenges.interfaces;
                    
                    public interface Year%1$s extends Year {
                    
                        @Override
                        default int getYear() {
                            return %1$s;
                        }
                    }
                    
                    """.formatted(year);

            writer.write(fileContent);
        }

    }

    private static void createChallenges(int year) throws IOException {

        Path folderPath = Path.of("src/main/java/challenges/y" + year);
        if (!Files.exists(folderPath)) {
            Files.createDirectory(folderPath);
        }

        for (int day = 1; day <= 25; day++) {
            String doubleDigitDay = String.format("%02d", day);

            Path challengeFilePath = Path.of("src/main/java/challenges/y" + year + "/AOC" + year + "Day" + doubleDigitDay + ".java");
            if (!Files.exists(challengeFilePath)) {
                Files.createFile(challengeFilePath);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(challengeFilePath)) {

                String fileContent = """
                        package challenges.y%1$s;
                        
                        import challenges.Challenge;
                        import challenges.interfaces.Day%2$s;
                        import challenges.interfaces.Year%1$s;
                        import challenges.params.ExecutionParameters;
                        
                        import java.util.List;
                        
                        public class AOC%1$sDay%2$s extends Challenge implements Year%1$s, Day%2$s {
                        
                            @Override
                            protected String solvePart1(String input, List<String> inputLines2, ExecutionParameters executionParameters) {
                                return NOT_IMPLEMENTED;
                            }
                        
                            @Override
                            protected String solvePart2(String input, List<String> inputLines2, ExecutionParameters executionParameters) {
                                return NOT_IMPLEMENTED;
                            }
                        
                        }
                        """.formatted(year, doubleDigitDay);

                writer.write(fileContent);
            }

        }

    }

    private static void createExamples(int year) throws IOException {

        Path yearFolderPath = Path.of("src/main/resources/examples/" + year);
        if (!Files.exists(yearFolderPath)) {
            Files.createDirectory(yearFolderPath);
        }

        for (int day = 1; day <= 25; day++) {
            String doubleDigitDay = String.format("%02d", day);

            Path dayFolderPath = Path.of("src/main/resources/examples/" + year + "/" + doubleDigitDay);
            if (!Files.exists(dayFolderPath)) {
                Files.createDirectory(dayFolderPath);
            }

            Path exampleFilePath = Path.of("src/main/resources/examples/" + year + "/" + doubleDigitDay + "/example.txt");
            if (!Files.exists(exampleFilePath)) {
                Files.createFile(exampleFilePath);
            }

        }

    }

    private static void createTests(int year) throws IOException {

        Path folderPath = Path.of("src/test/java/challenges/y" + year);
        if (!Files.exists(folderPath)) {
            Files.createDirectory(folderPath);
        }

        for (int day = 1; day <= 25; day++) {
            String doubleDigitDay = String.format("%02d", day);

            Path challengeFilePath = Path.of("src/test/java/challenges/y" + year + "/AOC" + year + "Day" + doubleDigitDay + "Test.java");
            if (!Files.exists(challengeFilePath)) {
                Files.createFile(challengeFilePath);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(challengeFilePath)) {

                String fileContent = """
                        package challenges.y%1$s;
                        
                        import org.junit.jupiter.api.MethodOrderer;
                        import org.junit.jupiter.api.Order;
                        import org.junit.jupiter.api.Test;
                        import org.junit.jupiter.api.TestMethodOrder;
                        
                        import java.util.Collections;
                        import java.util.List;
                        
                        import static org.junit.jupiter.api.Assertions.assertEquals;
                        
                        @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
                        class AOC%1$sDay%2$sTest {
                        
                            private static final AOC%1$sDay%2$s solution = new AOC%1$sDay%2$s();
                        
                            @Test
                            @Order(1)
                            void part1Examples() {
                                List<String> actualResults = solution.executeExamples(1);
                                List<String> expectedResults = Collections.singletonList("Not implemented");
                                assertEquals(expectedResults, actualResults);
                            }
                        
                            @Test
                            @Order(2)
                            void part1() {
                                assertEquals("Not implemented", solution.executePart1());
                            }
                        
                            @Test
                            @Order(3)
                            void part2Examples() {
                                List<String> actualResults = solution.executeExamples(2);
                                List<String> expectedResults = Collections.singletonList("Not implemented");
                                assertEquals(expectedResults, actualResults);
                            }
                        
                            @Test
                            @Order(4)
                            void part2() {
                                assertEquals("Not implemented", solution.executePart2());
                            }
                        }
                        """.formatted(year, doubleDigitDay);

                writer.write(fileContent);
            }

        }

    }

}
