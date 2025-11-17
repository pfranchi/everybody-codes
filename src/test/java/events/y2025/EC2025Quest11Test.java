package events.y2025;

import common.AbstractQuestTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class EC2025Quest11Test extends AbstractQuestTest {

    private static final EC2025Quest11 solution = new EC2025Quest11();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = List.of("109");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        executeQuest(solution, 1, "324");
    }

    @Test
    @Order(3)
    void part2Examples() {
        List<String> actualResults = solution.executeExamples(2);
        List<String> expectedResults = List.of("11", "1579");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(4)
    void part2() {
        executeQuest(solution, 2, "5691445");
    }

    @Test
    @Order(5)
    void part3Examples() {
        List<String> actualResults = solution.executeExamples(3);
        List<String> expectedResults = List.of("1");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(6)
    void part3() {
        executeQuest(solution, 3, "139309643324122");
    }


}

