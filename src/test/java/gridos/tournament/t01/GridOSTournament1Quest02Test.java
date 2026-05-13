package gridos.tournament.t01;

import gridos.tournaments.t01.GridOSTournament1Quest02;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class GridOSTournament1Quest02Test {

    private static final GridOSTournament1Quest02 PROGRAM = new GridOSTournament1Quest02();

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
