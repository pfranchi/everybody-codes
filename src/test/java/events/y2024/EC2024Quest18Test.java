package events.y2024;

import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class EC2024Quest18Test {

    private static final EC2024Quest18 solution = new EC2024Quest18();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = Collections.singletonList("11");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        assertEquals("109", solution.executePart1());
    }

    @Test
    @Order(3)
    void part2Examples() {
        List<String> actualResults = solution.executeExamples(2);
        List<String> expectedResults = Collections.singletonList("21");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(4)
    void part2() {
        assertEquals("2019", solution.executePart2());
    }

    @Test
    @Order(5)
    void part3Examples() {
        List<String> actualResults = solution.executeExamples(3);
        List<String> expectedResults = Collections.singletonList("12");
        assertEquals(expectedResults, actualResults);
    }

    @Disabled("Brute force approach takes about 1 minute and 30 seconds")
    @Test
    @Order(6)
    void part3() {
        assertEquals("258723", solution.executePart3());
    }


}
