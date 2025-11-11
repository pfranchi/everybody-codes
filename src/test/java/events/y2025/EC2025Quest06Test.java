package events.y2025;

import common.AbstractQuestTest;
import common.support.params.GenericExecutionParameter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class EC2025Quest06Test extends AbstractQuestTest {

    private static final EC2025Quest06 solution = new EC2025Quest06();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = List.of("5");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        executeQuest(solution, 1, "153");
    }

    @Test
    @Order(3)
    void part2Examples() {
        List<String> actualResults = solution.executeExamples(2);
        List<String> expectedResults = List.of("11");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(4)
    void part2() {
        executeQuest(solution, 2, "3794");
    }

    @Test
    @Order(5)
    void part3Examples() {

        EC2025Quest06.Part3Parameters example1Parameters = new EC2025Quest06.Part3Parameters(
                10, 1);

        EC2025Quest06.Part3Parameters example2Parameters = new EC2025Quest06.Part3Parameters(
                10, 2);

        EC2025Quest06.Part3Parameters example3Parameters = new EC2025Quest06.Part3Parameters(
                1000, 1000);

        List<String> actualResults = new ArrayList<>();

        actualResults.addAll(solution.executeExamples(3, new GenericExecutionParameter<>(example1Parameters),
                fn -> fn.startsWith("ex-1"), List.of()));
        actualResults.addAll(solution.executeExamples(3, new GenericExecutionParameter<>(example2Parameters),
                fn -> fn.startsWith("ex-2"), List.of()));
        actualResults.addAll(solution.executeExamples(3, new GenericExecutionParameter<>(example3Parameters),
                fn -> fn.startsWith("ex-3"), List.of()));

        List<String> expectedResults = List.of("34", "72", "3442321");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(6)
    void part3() {
        executeQuest(solution, 3, "1668727153");
    }


}

