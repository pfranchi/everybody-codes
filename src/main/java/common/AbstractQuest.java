package common;

import automation.model.PartDetail;
import automation.model.QuestDetail;
import common.support.interfaces.Event;
import common.support.interfaces.Quest;
import common.support.params.ExecutionParameters;
import automation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static java.util.concurrent.TimeUnit.*;

public abstract class AbstractQuest implements Event, Quest {

    private final Logger logger = LogManager.getLogger(this.getClass());

    protected static final String COMPLETED = "Completed";
    protected static final String NOT_IMPLEMENTED = "Not implemented";
    protected static final String EXCEPTION = "Exception";

    protected static final String VISUAL_SOLUTION = "Visual solution";

    // Implementations of parts 1, 2 and 3

    protected String solvePart1(String input, ExecutionParameters executionParameters) {
        return solvePart1(input, Strings.splitByRow(input), executionParameters);
    }

    protected abstract String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters);

    protected String solvePart2(String input, ExecutionParameters executionParameters) {
        return solvePart2(input, Strings.splitByRow(input), executionParameters);
    }

    protected abstract String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters);

    protected String solvePart3(String input, ExecutionParameters executionParameters) {
        return solvePart3(input, Strings.splitByRow(input), executionParameters);
    }

    protected abstract String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters);

    // Complete execution: input retrieval + time measurement

    public String executePart1() {
        return executePart1(null);
    }

    public String executePart1(ExecutionParameters executionParameters) {
        return executePart(1, input -> solvePart1(input, executionParameters));
    }

    public String executePart2() {
        return executePart2(null);
    }

    public String executePart2(ExecutionParameters executionParameters) {
        return executePart(2, input -> solvePart2(input, executionParameters));
    }

    public String executePart3() {
        return executePart3(null);
    }

    public String executePart3(ExecutionParameters executionParameters) {
        return executePart(3, input -> solvePart3(input, executionParameters));
    }

    private String executePart(int part, UnaryOperator<String> challengeSolution) {

        EventId eventId = getEventId();
        int questNumber = getQuestNumber();

        PuzzleId puzzleId = new PuzzleId(eventId, questNumber, part);

        QuestDetail questDetail = AutomationService.getInput(puzzleId);
        PartDetail partDetail = questDetail.getParts().get(part - 1);

        String input = partDetail.getDecrypted();

        log("START " + puzzleId);
        long start = System.nanoTime();
        String answer = challengeSolution.apply(input);
        long end = System.nanoTime();
        log("RESULT = " + answer);
        log("TIME = {}", toDisplayedTime(end - start));
        log("END " + puzzleId);

        boolean isAnswerCorrect = AutomationService.postAnswer(puzzleId, questDetail, partDetail, answer);

        log("Answer: {}", isAnswerCorrect ? "CORRECT" : "WRONG");
        emptyLine();

        return answer;

    }

    protected void log(Object o) {
        logger.info(o);
    }

    protected void log(String s) {
        logger.info(s);
    }

    protected void log(String msg, Object... args) {
        logger.info(msg, args);
    }

    protected void emptyLine() {
        log("");
    }

    protected void error(Exception e) {
        logger.error(e.getMessage());
    }

    public List<String> executeExamples(int part) {
        return executeExamples(part, (ExecutionParameters) null);
    }

    public List<String> executeExamples(int part, ExecutionParameters executionParameters) {
        return executeExamples(part, executionParameters, _ -> true, Collections.emptyList());
    }

    public List<String> executeExamples(int part, @Nonnull Predicate<String> includeFilenames) {
        return executeExamples(part, null, includeFilenames, Collections.emptyList());
    }

    public List<String> executeExamples(int part, ExecutionParameters executionParameters,
                                        @Nonnull Predicate<String> includeFilenames, @Nonnull Collection<String> excludeFilenames) {
        //int year = getEventYear();
        EventId eventId = getEventId();
        int day = getQuestNumber();

        PuzzleId puzzleId = new PuzzleId(eventId, day, part);

        UnaryOperator<String> challengeSolution = input -> switch (part) {
            case 1 -> solvePart1(input, executionParameters);
            case 2 -> solvePart2(input, executionParameters);
            case 3 -> solvePart3(input, executionParameters);
            default -> throw new IllegalArgumentException("Illegal part: " + part);
        };

        List<Example> examples = Examples.fetchExamples(puzzleId);

        if (examples.isEmpty()) {
            log("EXAMPLE " + puzzleId);
            log("No example for given criteria");
            return List.of();
        }

        List<String> results = new ArrayList<>();

        for (Example example : examples) {

            String filename = example.path().getFileName().toString();
            if (includeFilenames.test(filename) && !excludeFilenames.contains(filename)) {

                log("START " + puzzleId + " - EXAMPLE " + filename);
                String result = challengeSolution.apply(example.fileContent());
                log("RESULT = " + result);
                log("END " + puzzleId);
                emptyLine();

                results.add(result);

            }

        }

        return results;
    }

    private static String toDisplayedTime(long nanos) {
        TimeUnit unit = chooseUnit(nanos);
        double value = (double) nanos / NANOSECONDS.convert(1, unit);
        return String.format(Locale.ROOT, "%.4g", value) + " " + abbreviate(unit);
    }

    private static TimeUnit chooseUnit(long nanos) {
        if (DAYS.convert(nanos, NANOSECONDS) > 0) {
            return DAYS;
        }
        if (HOURS.convert(nanos, NANOSECONDS) > 0) {
            return HOURS;
        }
        if (MINUTES.convert(nanos, NANOSECONDS) > 0) {
            return MINUTES;
        }
        if (SECONDS.convert(nanos, NANOSECONDS) > 0) {
            return SECONDS;
        }
        if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) {
            return MILLISECONDS;
        }
        if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0) {
            return MICROSECONDS;
        }
        return NANOSECONDS;
    }

    private static String abbreviate(TimeUnit unit) {
        return switch (unit) {
            case NANOSECONDS -> "ns";
            case MICROSECONDS -> "μs"; // μs
            case MILLISECONDS -> "ms";
            case SECONDS -> "s";
            case MINUTES -> "min";
            case HOURS -> "h";
            case DAYS -> "d";
        };
    }

}
