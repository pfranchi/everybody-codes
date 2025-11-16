package events.y2025;

import common.AbstractQuestTest;
import common.support.params.ExecutionIntParameter;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class EC2025Quest10Test extends AbstractQuestTest {

    private static final EC2025Quest10 solution = new EC2025Quest10();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1, new ExecutionIntParameter(3));
        List<String> expectedResults = List.of("27");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        executeQuest(solution, 1, "162");
    }

    @Test
    @Order(3)
    void part2Examples() {
        List<String> actualResults = solution.executeExamples(2, new ExecutionIntParameter(3));
        List<String> expectedResults = List.of("27");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(4)
    void part2() {
        executeQuest(solution, 2, "1689");
    }

    @Test
    @Order(5)
    void part3Examples() {
        List<String> actualResults = solution.executeExamples(3);
        List<String> expectedResults = List.of("15", "8", "44", "4406", "13033988838");
        assertEquals(expectedResults, actualResults);
    }

    @Disabled("takes approximately 12 seconds")
    @Test
    @Order(6)
    void part3() {
        executeQuest(solution, 3, "7493438324610");
    }


}

