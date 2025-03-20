package challenges.ec2024;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class EC2024Quest17Test {

    private static final EC2024Quest17 solution = new EC2024Quest17();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = Collections.singletonList("16");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        assertEquals("138", solution.executePart1());
    }

    @Test
    @Order(3)
    void part2Examples() {
        List<String> actualResults = solution.executeExamples(2);
        List<String> expectedResults = Collections.singletonList("16");
        assertEquals(expectedResults, actualResults);
    }

    // Takes about 5 seconds
    @Test
    @Order(4)
    void part2() {
        assertEquals("1287", solution.executePart2());
    }

    @Test
    @Order(5)
    void part3Examples() {
        List<String> actualResults = solution.executeExamples(3);
        List<String> expectedResults = Collections.singletonList("15624");
        assertEquals(expectedResults, actualResults);
    }

    // Takes about 3 seconds
    @Test
    @Order(6)
    void part3() {
        assertEquals("4196767476", solution.executePart3());
    }


}
