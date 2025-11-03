package events.y2024;

import common.AbstractQuestTest;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class EC2024Quest11Test extends AbstractQuestTest {

    private static final EC2024Quest11 solution = new EC2024Quest11();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = Collections.singletonList("8");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        executeQuest(solution, 1, "34");
    }

    @Test
    @Order(3)
    void part2Examples() {
        List<String> actualResults = solution.executeExamples(2);
        List<String> expectedResults = Collections.singletonList("433585");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(4)
    void part2() {
        executeQuest(solution, 2, "433585");
    }

    @Test
    @Order(5)
    void part3Examples() {
        List<String> actualResults = solution.executeExamples(3);
        List<String> expectedResults = Collections.singletonList("268815");
        assertEquals(expectedResults, actualResults);
    }

    @Disabled
    @Test
    @Order(6)
    void part3() {
        executeQuest(solution, 3, "1373474665048");
    }


}
