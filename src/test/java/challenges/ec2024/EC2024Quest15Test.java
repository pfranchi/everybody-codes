package challenges.ec2024;

import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class EC2024Quest15Test {

    private static final EC2024Quest15 solution = new EC2024Quest15();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = Collections.singletonList("26");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        assertEquals("184", solution.executePart1());
    }

    @Test
    @Order(3)
    void part2Examples() {
        List<String> actualResults = solution.executeExamples(2);
        List<String> expectedResults = Collections.singletonList("38");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Disabled("Implementation too slow")
    @Order(4)
    void part2() {
        assertEquals("512", solution.executePart2());
    }

    @Test
    @Order(5)
    void part3Examples() {
        List<String> actualResults = solution.executeExamples(3);
        List<String> expectedResults = Collections.singletonList("Not implemented");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(6)
    void part3() {
        assertEquals("Not implemented", solution.executePart3());
    }


}
