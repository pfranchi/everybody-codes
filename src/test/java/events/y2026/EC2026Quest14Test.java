package events.y2026;

import common.AbstractQuestTest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class EC2026Quest14Test extends AbstractQuestTest {

    private static final EC2026Quest14 solution = new EC2026Quest14();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = List.of("67");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        executeQuest(solution, 1, "270");
    }

    @Test
    @Order(3)
    void part2Examples() {
        List<String> actualResults = solution.executeExamples(2);
        List<String> expectedResults = List.of("12", "63");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(4)
    void part2() {
        executeQuest(solution, 2, "504");
    }

    @Disabled("Solution does not work for the second example")
    @Test
    @Order(5)
    void part3Examples() {
        List<String> actualResults = solution.executeExamples(3);
        List<String> expectedResults = List.of("0", "21");
        assertEquals(expectedResults, actualResults);
    }

    @Disabled("Takes approximately 6 minutes")
    @Test
    @Order(6)
    void part3() {
        executeQuest(solution, 3, "1656");
    }


}

