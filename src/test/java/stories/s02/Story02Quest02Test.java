package stories.s02;

import common.AbstractQuestTest;
import common.support.params.ExecutionIntParameter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class Story02Quest02Test extends AbstractQuestTest {

    private static final Story02Quest02 solution = new Story02Quest02();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = List.of("7");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        executeQuest(solution, 1, "140");
    }

    @Test
    @Order(3)
    void part2Examples() {

        List<String> actualResults = new ArrayList<>();

        actualResults.addAll(solution.executeExamples(2, new ExecutionIntParameter(5), filename -> filename.startsWith("ex-1"), List.of()));
        actualResults.addAll(solution.executeExamples(2, new ExecutionIntParameter(10), filename -> filename.startsWith("ex-2"), List.of()));
        actualResults.addAll(solution.executeExamples(2, new ExecutionIntParameter(50), filename -> filename.startsWith("ex-2"), List.of()));
        actualResults.addAll(solution.executeExamples(2, new ExecutionIntParameter(100), filename -> filename.startsWith("ex-2"), List.of()));

        List<String> expectedResults = List.of("14", "304", "1464", "2955");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(4)
    void part2() {
        executeQuest(solution, 2, "21587");
    }

    @Test
    @Order(5)
    void part3Examples() {
        List<String> actualResults = new ArrayList<>();

        actualResults.addAll(solution.executeExamples(3, filename -> filename.startsWith("ex-1")));
        actualResults.addAll(solution.executeExamples(3, filename -> filename.startsWith("ex-2")));

        List<String> expectedResults = List.of("1094317", "600000");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(6)
    void part3() {
        executeQuest(solution, 3, "21225702");
    }


}
