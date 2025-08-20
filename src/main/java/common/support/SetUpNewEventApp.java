package common.support;

import automation.EventId;
import automation.EventType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SetUpNewEventApp {

    public static void main(String[] args) {

        EventType type = EventType.MAIN_EVENT;
        int year = 2043;
        int numberOfQuests = 20;

        EventId eventId = new EventId(type, year);

        try {

            createQuestDetails(eventId, numberOfQuests);
            createInterfaces(eventId, numberOfQuests);
            createChallenges(eventId, numberOfQuests);
            createExamples(eventId, numberOfQuests);
            createTests(eventId, numberOfQuests);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void createQuestDetails(EventId eventId, int numberOfQuests) throws IOException {

        String folderPathName = "src/main/resources/inputs/";

        if (eventId.type() == EventType.MAIN_EVENT) {
            folderPathName += "events/" + eventId.number();
        } else {
            folderPathName += "stories/" + String.format("%02d", eventId.number());
        }

        Path folderPath = Path.of(folderPathName);

        if (!Files.exists(folderPath)) {
            Files.createDirectory(folderPath);
        }

        for (int questNumber = 1; questNumber <= numberOfQuests; questNumber++) {

            Path path = Path.of(folderPathName + "/quest" + String.format("%02d", questNumber) + ".json");

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
                        """.formatted(eventId.number(), questNumber);

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(fileContent);
            }

        }

    }

    private static void createInterfaces(EventId eventId, int numberOfQuests) throws IOException {

        String superInterfaceName;
        String fileName;
        String methodName;

        if (eventId.type() == EventType.MAIN_EVENT) {
            superInterfaceName = "MainEvent";
            fileName = superInterfaceName + eventId.number();
            methodName = "getEventYear";
        } else {
            superInterfaceName = "Story";
            fileName = superInterfaceName + String.format("%02d", eventId.number());
            methodName = "getStoryNumber";
        }

        Path eventInterfacePath = Path.of("src/main/java/common/support/interfaces/" + fileName + ".java");
        if (!Files.exists(eventInterfacePath)) {
            Files.createFile(eventInterfacePath);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(eventInterfacePath)) {

            String fileContent = """
                    package common.support.interfaces;
                    
                    public interface %1$s extends %2$s {
                    
                        @Override
                        default int %3$s() {
                            return %4$s;
                        }
                    
                    }
                    
                    """.formatted(fileName, superInterfaceName, methodName, eventId.number());

            writer.write(fileContent);
        }

        for (int questNumber = 1; questNumber <= numberOfQuests; questNumber++) {

            String questInterfacePathName = "src/main/java/common/support/interfaces/Quest" + String.format("%02d", questNumber) + ".java";
            Path questInterfacePath = Path.of(questInterfacePathName);

            if (!Files.exists(questInterfacePath)) {
                Files.createFile(questInterfacePath);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(questInterfacePath)) {

                String fileContent = """
                        package common.support.interfaces;
                        
                        public interface Quest%1$s extends Quest {
                        
                            @Override
                            default int getQuestNumber() {
                                return %2$s;
                            }
                        
                        }
                        """.formatted(String.format("%02d", questNumber), questNumber);

                writer.write(fileContent);

            }

        }

    }

    private static void createChallenges(EventId eventId, int numberOfQuests) throws IOException {

        String folderPathName = "src/main/java/";
        String packageName;
        String eventInterfaceName;

        if (eventId.type() == EventType.MAIN_EVENT) {
            folderPathName += "/events/y" + eventId.number();
            packageName = "events.y" + eventId.number();
            eventInterfaceName = "MainEvent" + eventId.number();
        } else {

            String doubleDigitStoryNumber = String.format("%02d", eventId.number());

            folderPathName += "/stories/s" + doubleDigitStoryNumber;
            packageName = "stories.s" + doubleDigitStoryNumber;
            eventInterfaceName = "Story" + doubleDigitStoryNumber;
        }

        Path folderPath = Path.of(folderPathName);
        if (!Files.exists(folderPath)) {
            Files.createDirectory(folderPath);
        }

        String baseClassName;
        if (eventId.type() == EventType.MAIN_EVENT) {
            baseClassName = "EC" + eventId.number();
        } else {
            baseClassName = "Story" + String.format("%02d", eventId.number());
        }

        for (int questNumber = 1; questNumber <= numberOfQuests; questNumber++) {

            String doubleDigitQuestNumber = String.format("%02d", questNumber);
            String className = baseClassName + "Quest" + doubleDigitQuestNumber;
            String challengeFilePathName = folderPathName + "/" + className + ".java";

            Path challengeFilePath = Path.of(challengeFilePathName);

            if (!Files.exists(challengeFilePath)) {
                Files.createFile(challengeFilePath);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(challengeFilePath)) {

                String fileContent = """
                        package %1$s;
                        
                        import common.AbstractQuest;
                        import common.support.interfaces.Quest%2$s;
                        import common.support.interfaces.%3$s;
                        import common.support.params.ExecutionParameters;
                        
                        import java.util.List;
                        
                        public class %4$s extends AbstractQuest implements %3$s, Quest%2$s {
                        
                            @Override
                            protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
                        
                                inputLines.forEach(this::log);
                        
                                return NOT_IMPLEMENTED;
                            }
                        
                            @Override
                            protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
                                return NOT_IMPLEMENTED;
                            }
                        
                            @Override
                            protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
                                return NOT_IMPLEMENTED;
                            }
                        
                        }
                        """.formatted(packageName, doubleDigitQuestNumber, eventInterfaceName, className);

                writer.write(fileContent);
            }

        }

    }

    private static void createExamples(EventId eventId, int numberOfQuests) throws IOException {

        String eventFolderPathName = "src/main/resources/examples/";

        if (eventId.type() == EventType.MAIN_EVENT) {
            eventFolderPathName += "events/" + eventId.number();
        } else {
            eventFolderPathName += "stories/" + String.format("%02d", eventId.number());
        }

        Path folderPath = Path.of(eventFolderPathName);
        if (!Files.exists(folderPath)) {
            Files.createDirectory(folderPath);
        }

        for (int questNumber = 1; questNumber <= numberOfQuests; questNumber++) {
            String doubleDigitQuestNumber = String.format("%02d", questNumber);

            String questFolderPathName = eventFolderPathName + "/quest" + doubleDigitQuestNumber;

            Path questFolderPath = Path.of(questFolderPathName);
            if (!Files.exists(questFolderPath)) {
                Files.createDirectory(questFolderPath);
            }

            for (int part = 1; part <= 3; part++) {

                String partPathName = questFolderPathName + "/part" + part;
                Path partPath = Path.of(partPathName);
                if (!Files.exists(partPath)) {
                    Files.createDirectory(partPath);
                }

                Path exampleFilePath = Path.of(partPathName + "/example.txt");
                if (!Files.exists(exampleFilePath)) {
                    Files.createFile(exampleFilePath);
                }

            }

        }

    }

    private static void createTests(EventId eventId, int numberOfQuests) throws IOException {

        String folderPathName = "src/test/java/";
        String packageName;
        String eventInterfaceName;

        if (eventId.type() == EventType.MAIN_EVENT) {
            folderPathName += "/events/y" + eventId.number();
            packageName = "events.y" + eventId.number();
            eventInterfaceName = "MainEvent" + eventId.number();
        } else {

            String doubleDigitStoryNumber = String.format("%02d", eventId.number());

            folderPathName += "/stories/s" + doubleDigitStoryNumber;
            packageName = "stories.s" + doubleDigitStoryNumber;
            eventInterfaceName = "Story" + doubleDigitStoryNumber;
        }

        Path folderPath = Path.of(folderPathName);
        if (!Files.exists(folderPath)) {
            Files.createDirectory(folderPath);
        }

        String baseClassName;
        if (eventId.type() == EventType.MAIN_EVENT) {
            baseClassName = "EC" + eventId.number();
        } else {
            baseClassName = "Story" + String.format("%02d", eventId.number());
        }

        for (int questNumber = 1; questNumber <= numberOfQuests; questNumber++) {

            String doubleDigitQuestNumber = String.format("%02d", questNumber);
            String className = baseClassName + "Quest" + doubleDigitQuestNumber;
            String testClassName = className + "Test";
            String challengeFilePathName = folderPathName + "/" + testClassName + ".java";

            Path challengeFilePath = Path.of(challengeFilePathName);

            if (!Files.exists(challengeFilePath)) {
                Files.createFile(challengeFilePath);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(challengeFilePath)) {

                String fileContent = """
                        package %1$s;
                        
                        import common.AbstractQuestTest;
                        import org.junit.jupiter.api.MethodOrderer;
                        import org.junit.jupiter.api.Order;
                        import org.junit.jupiter.api.Test;
                        import org.junit.jupiter.api.TestMethodOrder;
                        
                        import java.util.List;
                        
                        import static org.junit.jupiter.api.Assertions.assertEquals;
                        
                        @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
                        class %2$s extends AbstractQuestTest {
                        
                            private static final %3$s solution = new %3$s();
                        
                            @Test
                            @Order(1)
                            void part1Examples() {
                                List<String> actualResults = solution.executeExamples(1);
                                List<String> expectedResults = List.of("Not implemented");
                                assertEquals(expectedResults, actualResults);
                            }
                        
                            @Test
                            @Order(2)
                            void part1() {
                                executeQuest(solution, 1, "Not implemented");
                            }
                        
                            @Test
                            @Order(3)
                            void part2Examples() {
                                List<String> actualResults = solution.executeExamples(2);
                                List<String> expectedResults = List.of("Not implemented");
                                assertEquals(expectedResults, actualResults);
                            }
                        
                            @Test
                            @Order(4)
                            void part2() {
                                executeQuest(solution, 2, "Not implemented");
                            }
                        
                            @Test
                            @Order(5)
                            void part3Examples() {
                                List<String> actualResults = solution.executeExamples(3);
                                List<String> expectedResults = List.of("Not implemented");
                                assertEquals(expectedResults, actualResults);
                            }
                        
                            @Test
                            @Order(6)
                            void part3() {
                                executeQuest(solution, 3, "Not implemented");
                            }
                        
                        
                        }
                        
                        """.formatted(packageName, testClassName, className);

                writer.write(fileContent);
            }

        }

    }

}
