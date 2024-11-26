package challenges.ec2024;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class EC2024Quest11Test {

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
        assertEquals("34", solution.executePart1());
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
        assertEquals("433585", solution.executePart2());
    }

    @Test
    @Order(5)
    void part3Examples() {
        List<String> actualResults = solution.executeExamples(3);
        List<String> expectedResults = Collections.singletonList("268815");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(6)
    void part3() {
        assertEquals("Not implemented", solution.executePart3());
    }


}
