package events.y2026;

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
class EC2026Quest10Test extends AbstractQuestTest {

    private static final EC2026Quest10 solution = new EC2026Quest10();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = List.of("29");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        executeQuest(solution, 1, "63");
    }

    @Test
    @Order(3)
    void part2Examples() {

        List<String> resultsWithLoweredLimit =
                solution.executeExamples(2, new ExecutionIntParameter(10));
        List<String> resultWithStandardLimit = solution.executeExamples(2);

        List<String> actualResults = new ArrayList<>(resultsWithLoweredLimit);
        actualResults.addAll(resultWithStandardLimit);

        List<String> expectedResults = List.of("53", "50000");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(4)
    void part2() {
        executeQuest(solution, 2, "132565");
    }

    @Test
    @Order(5)
    void part3Examples() {
        List<String> actualResults = solution.executeExamples(3);
        List<String> expectedResults = List.of("13314695352588");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(6)
    void part3() {
        executeQuest(solution, 3, "13857142856992");
    }


}

