package gridos.tournament.t01;

import gridos.tournaments.t01.GridOSTournament1Quest05;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class GridOSTournament1Quest05Test {

    private static final GridOSTournament1Quest05 PROGRAM = new GridOSTournament1Quest05();

    @Test
    @Order(1)
    void executePart1() {
        PROGRAM.executePart1();
    }

    @Test
    @Order(2)
    void executePart2() {
        PROGRAM.executePart2();
    }

    @Test
    @Order(3)
    void executePart3() {
        PROGRAM.executePart3();
    }

}
