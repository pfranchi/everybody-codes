package stories.s04;

import common.AbstractQuestTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class Story04Quest01Test extends AbstractQuestTest {

    private static final Story04Quest01 solution = new Story04Quest01();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = List.of("66", "34");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        executeQuest(solution, 1, "294");
    }

    @Test
    @Order(3)
    void part2Examples() {
        List<String> actualResults = solution.executeExamples(2);
        List<String> expectedResults = List.of("43");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(4)
    void part2() {
        executeQuest(solution, 2, "32656");
    }

    @Test
    @Order(5)
    void part3Examples() {
        List<String> actualResults = solution.executeExamples(3, filename -> filename.startsWith("ex-3"));
        List<String> expectedResults = List.of("27", "35");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(6)
    void part3() {
        executeQuest(solution, 3, "Not implemented");
    }


}

