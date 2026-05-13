package common.gridos;

import common.geo.ImmutableCell2D;

import java.util.List;
import java.util.Map;

public interface GridOSTestCase {

    List<String> getCharGrid();

    /*
        Returns the positions A, B, C...
     */
    Map<Character, ImmutableCell2D> getPositions();

    record PersonalFormat(List<String> charGrid, Map<Character, ImmutableCell2D> positions) implements GridOSTestCase {

        @Override
        public List<String> getCharGrid() {
            return charGrid;
        }

        @Override
        public Map<Character, ImmutableCell2D> getPositions() {
            return positions;
        }

    }

}
