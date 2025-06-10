package events.y2024;

import common.AbstractQuestTest;
import common.support.params.ExecutionParameters;
import common.support.params.GenericExecutionParameter;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class EC2024Quest07Test extends AbstractQuestTest {

    private static final EC2024Quest07 solution = new EC2024Quest07();

    @Test
    @Order(1)
    void part1Examples() {
        List<String> actualResults = solution.executeExamples(1);
        List<String> expectedResults = Collections.singletonList("BDCA");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(2)
    void part1() {
        executeQuest(solution, 1, "EHKICJDGA");
    }

    @Test
    @Order(3)
    void part2Examples() {

        String exampleTrack = """
                S+===
                -   +
                =+=-+""";

        ExecutionParameters parameters = new GenericExecutionParameter<>(exampleTrack);

        List<String> actualResults = solution.executeExamples(2, parameters);
        List<String> expectedResults = Collections.singletonList("DCBA");
        assertEquals(expectedResults, actualResults);
    }

    @Test
    @Order(4)
    void part2() {
        executeQuest(solution, 2, "JGBKEAIFD");
    }

    @Disabled
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
        executeQuest(solution, 1, "Not implemented");
    }


}
