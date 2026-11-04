package events.y2026;

import common.AbstractQuestTest;
import common.support.params.ExecutionParameters;
import common.support.params.GenericExecutionParameter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class EC2026Quest07Test extends AbstractQuestTest {

    private static final EC2026Quest07 solution = new EC2026Quest07();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = List.of("102");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        executeQuest(solution, 1, "285");
    }

    @Test
    @Order(3)
    void part2Examples() {
        List<String> actualResults = solution.executeExamples(2);
        List<String> expectedResults = List.of("1316", "1214633");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(4)
    void part2() {
        executeQuest(solution, 2, "148763129");
    }

    @Test
    @Order(5)
    void part3Examples() {

        String example1Result = solution
                .executeExamples(3, new GenericExecutionParameter<>(100L), filename -> filename.startsWith("ex-1"), List.of())
                .getFirst();

        String example2Result = solution.executeExamples(3, filename -> filename.startsWith("ex-2")).getFirst();

        List<String> actualResults = List.of(example1Result, example2Result);
        List<String> expectedResults = List.of("187", "34777023");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(6)
    void part3() {
        executeQuest(solution, 3, "31539125");
    }


}

