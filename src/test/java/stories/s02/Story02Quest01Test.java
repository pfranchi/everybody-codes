package stories.s02;

import common.AbstractQuestTest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class Story02Quest01Test extends AbstractQuestTest {

    private static final Story02Quest01 solution = new Story02Quest01();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = List.of("26");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        executeQuest(solution, 1, "52");
    }

    @Test
    @Order(3)
    void part2Examples() {
        List<String> actualResults = solution.executeExamples(2);
        List<String> expectedResults = List.of("115");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(4)
    void part2() {
        executeQuest(solution, 2, "1066");
    }

    @Disabled("implementation not available yet")
    @Test
    @Order(5)
    void part3Examples() {
        List<String> actualResults = solution.executeExamples(3);
        List<String> expectedResults = List.of("13 43", "25 66", "39 122");
        assertEquals(expectedResults, actualResults);
    }

    @Disabled("solution found by hand")
    @Test
    @Order(6)
    void part3() {
        executeQuest(solution, 3, "34 109");
    }


}
