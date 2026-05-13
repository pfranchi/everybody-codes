package common.gridos;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import common.geo.ImmutableCell2D;
import common.geo.MutableCell2D;
import common.stats.Cell2DMinMaxStatistics;
import common.stats.IntMinMaxStatistics;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GridOSProgramRun {

    private final GridOSProgram program;
    private final int numberOfHeads;

    public GridOSProgramRun(GridOSProgram program) {
        this.program = program;
        this.numberOfHeads = program.getNumberOfHeads();
    }

    private static class Head {
        private MutableCell2D location;

    }
    private String currentState;
    private List<Head> heads;
    private Map<ImmutableCell2D, Character> characterGrid;

    public void initializeRun(GridOSTestCase testCase) {

        currentState = "START";

        List<String> charGrid = testCase.getCharGrid();
        Map<Character, ImmutableCell2D> positions = testCase.getPositions();

        // Initialize the heads

        this.heads = new ArrayList<>();
        List<Character> headStartingPositions = program.getHeadStartingPositions();

        for (char headStartingPosition: headStartingPositions) {
            Head head = new Head();
            head.location = MutableCell2D.copyOf(positions.get(headStartingPosition));
            heads.add(head);
        }

        int numberOfRows = charGrid.size();

        characterGrid = new HashMap<>();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            char[] lineCharArray = charGrid.get(rowIndex).toCharArray();

            int numberOfColumns = lineCharArray.length;

            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                characterGrid.put(ImmutableCell2D.of(rowIndex, columnIndex), lineCharArray[columnIndex]);
            }

        }

    }

    public GridOSExecutionResult execute() {

        //System.out.println("Start execution");

        Set<String> usedStates = new HashSet<>();
        usedStates.add(currentState);
        Set<GridOSProgram.Rule> usedRules = new HashSet<>();
        long stepCounter = 0;

        while (!currentState.equals("STOP")) {

            // Perform one step
            stepCounter++;

            List<Character> charactersCurrentlyUnderHeads = heads.stream()
                    .map(head -> characterGrid.getOrDefault(ImmutableCell2D.copyOf(head.location), '\u0000'))
                    .toList();

            // Check which rule can be applied. If zero rules or more than one rule apply, raise an error.
            List<GridOSProgram.Rule> appliableRules = program.getRules().stream()
                    .filter(rule -> rule.canBeApplied(currentState, charactersCurrentlyUnderHeads))
                    .toList();

            if (appliableRules.size() != 1) {
                String message;
                if (appliableRules.isEmpty()) {
                    message = "No rule can be applied";
                } else {
                    message = "More than one rule can be applied";
                }
                throw new IllegalStateException(message);
            }

            GridOSProgram.Rule appliableRule =  appliableRules.getFirst();
            usedRules.add(appliableRule);

            List<Character> newValuesWritten = appliableRule.writeNewValues(charactersCurrentlyUnderHeads);
            List<GridOSProgram.Movement> movements = appliableRule.getMovements();

            // Write to the grid and move the heads
            for (int headIndex = 0; headIndex < numberOfHeads; headIndex++) {
                Head head = heads.get(headIndex);
                char newValueToBeWritten = newValuesWritten.get(headIndex);
                characterGrid.put(ImmutableCell2D.copyOf(head.location), newValueToBeWritten);
                movements.get(headIndex).move(head.location);
            }

            // Change the state
            this.currentState = appliableRule.getWriteState();
            usedStates.add(currentState);

        }

        List<String> finalGrid = new ArrayList<>();

        Cell2DMinMaxStatistics cellStats =  new Cell2DMinMaxStatistics();

        Table<Integer, Integer, Character> gridTable = TreeBasedTable.create();
        for (Map.Entry<ImmutableCell2D, Character> entry: characterGrid.entrySet()) {
            ImmutableCell2D cell = entry.getKey();
            cellStats.accept(cell);
            gridTable.put(cell.row(), cell.column(), entry.getValue());
        }

        IntMinMaxStatistics rowStatistics = cellStats.getRowStatistics();
        IntMinMaxStatistics columnStatistics = cellStats.getColumnStatistics();

        for (int rowIndex = rowStatistics.getMin(); rowIndex <= rowStatistics.getMax(); rowIndex++) {

            SortedMap<Integer, Character> rowMap = (SortedMap<Integer, Character>) gridTable.row(rowIndex);
            StringBuilder result = new StringBuilder();
            for (int columnIndex = columnStatistics.getMin(); columnIndex <= rowMap.lastKey(); columnIndex++) {
                char c = rowMap.getOrDefault(columnIndex, ' ');
                if (c == '\u0000') {
                    c = ' ';
                }
                result.append(c);
            }

            finalGrid.add(result.toString().stripTrailing());

        }

        return new GridOSExecutionResult(numberOfHeads, usedStates.size(), usedRules.size(), stepCounter, finalGrid);

    }

}
