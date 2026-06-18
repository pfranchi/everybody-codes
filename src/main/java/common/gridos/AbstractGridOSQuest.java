package common.gridos;

import common.Sections;
import common.geo.ImmutableCell2D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class AbstractGridOSQuest {

    private final Logger logger = LogManager.getLogger(this.getClass());

    protected abstract String getFolderPath();

    public void executePart1() {
        execute(1);
    }

    public void executePart2() {
        execute(2);
    }

    public void executePart3() {
        execute(3);
    }

    private List<GridOSProgram> loadProgram(String folderPath) {

        List<GridOSProgram> programs = new ArrayList<>();

        try (Stream<Path> paths = Files.list(Path.of(folderPath))) {

            for (Path filePath: paths.toList()) {
                String fileName = filePath.getFileName().toString();
                if (fileName.startsWith("program")) {

                    // This is one of the programs

                    GridOSProgram program = new GridOSProgram();
                    List<String> lines = Files.readAllLines(filePath);
                    program.loadProgram(fileName, lines);

                    programs.add(program);

                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return programs;
    }

    private List<GridOSTestCase> loadTestCases(String folderPath) {

        List<GridOSTestCase> testCases = new ArrayList<>();

        try (Stream<Path> paths = Files.list(Path.of(folderPath))) {

            for (Path filePath: paths.toList()) {
                if (!filePath.getFileName().toString().startsWith("program")) {

                    // This is a valid test case

                    String fileContent = Files.readString(filePath);
                    if (fileContent.startsWith("######")) {

                        List<List<String>> sections = new ArrayList<>(Sections.splitIntoSections(fileContent, line -> line.equals("######")));
                        sections.removeFirst();

                        List<String> firstSection = sections.removeFirst();
                        List<String> secondSection = sections.removeFirst();

                        Map<Character, ImmutableCell2D> positions = new HashMap<>();
                        for (String secondSectionLine: secondSection) {
                            String[] parts = secondSectionLine.split(":");
                            char letter = parts[0].charAt(0);
                            String[] coordinates = parts[1].split(";");
                            int rowIndex = Integer.parseInt(coordinates[0]);
                            int colIndex = Integer.parseInt(coordinates[1]);
                            positions.put(letter, ImmutableCell2D.of(rowIndex, colIndex));
                        }

                        testCases.add(new GridOSTestCase.PersonalFormat(firstSection, positions));

                    } else {
                        throw new UnsupportedOperationException("The only supported format for test case files is the ###### format");
                    }

                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return testCases;

    }

    private void execute(int part) {

        String folderPath = "src/main/resources/" + getFolderPath() + "/part" + part;
        List<GridOSProgram> programs = loadProgram(folderPath);
        List<GridOSTestCase> testCases = loadTestCases(folderPath);
        int numberOfTestCases = testCases.size();

        for (GridOSProgram program: programs) {

            GridOSProgramRun programRun = new GridOSProgramRun(program);

            GridOSExecutionResult totalResult = new GridOSExecutionResult(0, 0, 0, 0, null);

            logger.info("Starting execution of {} test cases. Program: {}", numberOfTestCases, program.getProgramName());

            for (int testCaseIndex = 0; testCaseIndex < numberOfTestCases; testCaseIndex++) {

                logger.info("");
                logger.info("START Test case #{}/{}. Starting grid is:", testCaseIndex + 1, numberOfTestCases);

                GridOSTestCase testCase = testCases.get(testCaseIndex);
                for (String line : testCase.getCharGrid()) {
                    logger.info(line);
                }
                programRun.initializeRun(testCase);
                GridOSExecutionResult executionResult = programRun.execute();

                int heads = executionResult.numberOfHeads();
                int states = executionResult.numberOfStates();
                int rules = executionResult.numberOfUsedRules();
                long steps = executionResult.numberOfSteps();

                logger.info("END Test case #{}/{}. Heads = {}, states = {}, rules = {}, steps = {}. Final grid is:",
                        testCaseIndex + 1, numberOfTestCases, heads, states, rules, steps);

                List<String> finalGrid = executionResult.grid();
                for (String line : finalGrid) {
                    logger.info(line);
                }

                totalResult = new GridOSExecutionResult(
                        totalResult.numberOfHeads() + heads,
                        totalResult.numberOfStates() + states,
                        totalResult.numberOfUsedRules() + rules,
                        totalResult.numberOfSteps() + steps,
                        null);

            }

            logger.info("");
            logger.info("All {} test cases completed for program {}. Heads = {}, states = {}, rules = {}, steps = {}",
                    numberOfTestCases, program.getProgramName(), totalResult.numberOfHeads(), totalResult.numberOfStates(),
                    totalResult.numberOfUsedRules(), totalResult.numberOfSteps());

        }

        //return totalResult;

    }


}
