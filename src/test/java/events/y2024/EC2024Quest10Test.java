package events.y2024;

import common.AbstractQuestTest;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class EC2024Quest10Test extends AbstractQuestTest {

    private static final EC2024Quest10 solution = new EC2024Quest10();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = Collections.singletonList("PTBVRCZHFLJWGMNS");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        executeQuest(solution, 1, "BHTJDMVFSGWKXPQN");
    }

    @Test
    @Order(3)
    void part2Examples() {
        List<String> actualResults = solution.executeExamples(2);
        List<String> expectedResults = Collections.singletonList("1851");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(4)
    void part2() {
        executeQuest(solution, 2, "199395");
    }

    @Test
    @Order(5)
    void part3Examples() {
        List<String> actualResults = solution.executeExamples(3);
        List<String> expectedResults = Collections.singletonList("Not implemented");
        assertEquals(expectedResults, actualResults);
    }

    @Disabled("Not implemented")
    @Test
    @Order(6)
    void part3() {
        executeQuest(solution, 3, "Not implemented");
    }


}
