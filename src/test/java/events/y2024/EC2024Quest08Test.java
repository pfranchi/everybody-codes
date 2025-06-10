package events.y2024;

import common.AbstractQuestTest;
import common.support.params.GenericExecutionParameter;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class EC2024Quest08Test extends AbstractQuestTest {

    private static final EC2024Quest08 solution = new EC2024Quest08();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = Collections.singletonList("21");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        executeQuest(solution, 1, "8288303");
    }

    @Test
    @Order(3)
    void part2Examples() {

        EC2024Quest08.Quest8Input input = new EC2024Quest08.Quest8Input(5, 50);

        List<String> actualResults = solution.executeExamples(2, new GenericExecutionParameter<>(input));
        List<String> expectedResults = Collections.singletonList("27");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(4)
    void part2() {
        executeQuest(solution, 2, "104365988");
    }

    @Test
    @Order(5)
    void part3Examples() {
        List<String> actualResults = solution.executeExamples(3);
        List<String> expectedResults = Collections.singletonList("Not implemented");
        assertEquals(expectedResults, actualResults);
    }

    @Disabled
    @Test
    @Order(6)
    void part3() {
        executeQuest(solution, 3, "Not implemented");
    }


}
