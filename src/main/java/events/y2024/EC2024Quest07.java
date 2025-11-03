package events.y2024;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;
import common.AbstractQuest;
import common.Grids;
import common.Strings;
import common.geo.CardinalDirection2D;
import common.geo.ImmutableCell2D;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest07;
import common.support.params.ExecutionParameters;
import common.support.params.GenericExecutionParameter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;

import static events.y2024.EC2024Quest07.Action.*;
import static events.y2024.EC2024Quest07.TrackAction.END;

public class EC2024Quest07 extends AbstractQuest implements MainEvent2024, Quest07 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ListMultimap<String, Action> planIdToActions = createPlanActions(inputLines);

        Map<String, Long> totalEssences = new HashMap<>();

        for (Map.Entry<String, Collection<Action>> entry: planIdToActions.asMap().entrySet()) {

            String planId = entry.getKey();
            List<Action> actions = (List<Action>) entry.getValue();
            int numberOfActions = actions.size();

            long currentValue = 10; // Start value

            List<Long> values = new ArrayList<>();

            for (int segment = 0; segment < 10; segment++) {
                Action currentAction = actions.get( segment % numberOfActions );
                currentValue = currentAction.applyAsLong(currentValue);
                values.add(currentValue);
            }

            long totalEssence = values.stream().mapToLong(i -> i).sum();
            totalEssences.put(planId, totalEssence);

        }

        return getRankings(totalEssences);

    }


    public enum Action {

        PLUS(operand -> operand + 1, "+"),
        MINUS(operand -> Long.max(operand - 1, 0), "-"),
        MAINTAIN(LongUnaryOperator.identity(), "=");

        private final LongUnaryOperator operator;
        private final String symbol;

        Action(LongUnaryOperator operator, String symbol) {
            this.operator = operator;
            this.symbol = symbol;
        }

        public long applyAsLong(long operand) {
            return operator.applyAsLong(operand);
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    private ListMultimap<String, Action> createPlanActions(List<String> inputLines) {
        ListMultimap<String, Action> planIdToActions = ArrayListMultimap.create();

        for (String inputLine: inputLines) {

            String[] parts = inputLine.split(":");
            String planId = parts[0];
            String[] actionCodes = parts[1].split(",");

            for (String actionCode: actionCodes) {

                Action operator = switch (actionCode.charAt(0)) {
                    case '+' -> PLUS;
                    case '-' -> MINUS;
                    case '=' -> MAINTAIN;
                    default -> throw new IllegalArgumentException("Invalid action: " + actionCode);
                };

                planIdToActions.put(planId, operator);

            }

        }

        return planIdToActions;
    }

    private String getRankings(Map<String, Long> totalEssences) {
        Map<Long, String> rankings = new TreeMap<>(Comparator.reverseOrder());

        for (Map.Entry<String, Long> entry: totalEssences.entrySet()) {
            String planId = entry.getKey();
            long totalEssence = entry.getValue();

            if (rankings.containsKey(totalEssence)) {
                throw new IllegalStateException("There is a tie with total essence " + totalEssence + " for plans " + rankings.get(totalEssence) + " and " + planId);
            }

            rankings.put(totalEssence, planId);

        }

        return String.join("", rankings.values());
    }

    private String PART_2_TRACK = """
            S-=++=-==++=++=-=+=-=+=+=--=-=++=-==++=-+=-=+=-=+=+=++=-+==++=++=-=-=--
            -                                                                     -
            =                                                                     =
            +                                                                     +
            =                                                                     +
            +                                                                     =
            =                                                                     =
            -                                                                     -
            --==++++==+=+++-=+=-=+=-+-=+-=+-=+=-=+=--=+++=++=+++==++==--=+=++==+++-""";


    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        String track = PART_2_TRACK;
        if (executionParameters instanceof GenericExecutionParameter<?>(String value)) {
            track = value;
        }

        String rectifiedTrack = rectifyTrack(track);

        List<TrackAction> trackActions = new ArrayList<>();

        for (char c: rectifiedTrack.toCharArray()) {
            trackActions.add(TrackAction.fromChar(c));
        }
        trackActions.add(END);

        ListMultimap<String, Action> planIdToActions = createPlanActions(inputLines);
        Map<String, Long> totalEssences = new HashMap<>();

        for (Map.Entry<String, Collection<Action>> entry: planIdToActions.asMap().entrySet()) {
            String planId = entry.getKey();
            List<Action> planActions = (List<Action>) entry.getValue();

            long total = executePlan(planActions, trackActions, 10); // 10 loops of the track

            totalEssences.put(planId, total);

            log("Plan {} gathers {} total essence", planId, total);

        }

        return getRankings(totalEssences);
    }

    private static long executePlan(List<Action> planActions, List<TrackAction> trackActions, int numberOfTrackLoops) {

        int numberOfActions = planActions.size();

        int trackLength = trackActions.size();

        long currentValue = 10L;
        long totalNumberOfSteps = 0;
        long total = 0;

        for (int trackLoopNumber = 1; trackLoopNumber <= numberOfTrackLoops; trackLoopNumber++) {

            for (int trackSegment = 0; trackSegment < trackLength; trackSegment++) {

                TrackAction trackAction = trackActions.get((int) (totalNumberOfSteps % trackLength));
                Action planAction = planActions.get((int) (totalNumberOfSteps % numberOfActions));

                Action actualAction = trackAction.transform(planAction);

                currentValue = actualAction.applyAsLong(currentValue);

                totalNumberOfSteps++;
                total += currentValue;

            }

        }
        return total;
    }

    private String rectifyTrack(String track) {

        List<String> trackLines = Strings.splitByRow(track);
        int numberOfRows = trackLines.size();
        int numberOfColumns = trackLines.getFirst().length();

        StringBuilder sb = new StringBuilder();

        // Top row
        sb.append(trackLines.getFirst().substring(1)); // Removing the starting S

        // Right column
        for (int row = 1; row < numberOfRows - 1; row++) {
            sb.append(trackLines.get(row).charAt(numberOfColumns - 1));
        }

        // Bottom row
        sb.append(StringUtils.reverse(trackLines.getLast()));

        // Left column
        for (int row = numberOfRows - 2; row > 0; row--) {
            sb.append(trackLines.get(row).charAt(0));
        }

        return sb.toString();

    }

    public enum TrackAction {

        FORCED_PLUS(_ -> PLUS, "+"),
        FORCED_MINUS(_ -> MINUS, "-"),
        UNALTERED(UnaryOperator.identity(), "="),
        END(UnaryOperator.identity(), "E");

        private final UnaryOperator<Action> operatorTransformator;
        private final String symbol;

        TrackAction(UnaryOperator<Action> operatorTransformator, String symbol) {
            this.operatorTransformator = operatorTransformator;
            this.symbol = symbol;
        }

        public static TrackAction fromChar(char c) {
            return switch (c) {
                case '+' -> FORCED_PLUS;
                case '-' -> FORCED_MINUS;
                case '=' -> UNALTERED;
                default -> throw new IllegalArgumentException("Invalid character in the track: " + c);
            };
        }

        public Action transform(Action action) {
            return operatorTransformator.apply(action);
        }

        @Override
        public String toString() {
            return symbol;
        }

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solvePart3Attempt1(inputLines);
    }

    private String solvePart3Attempt1(List<String> inputLines) {
        String track = """
                S+= +=-== +=++=     =+=+=--=    =-= ++=     +=-  =+=++=-+==+ =++=-=-=--
                - + +   + =   =     =      =   == = - -     - =  =         =-=        -
                = + + +-- =-= ==-==-= --++ +  == == = +     - =  =    ==++=    =++=-=++
                + + + =     +         =  + + == == ++ =     = =  ==   =   = =++=
                = = + + +== +==     =++ == =+=  =  +  +==-=++ =   =++ --= + =
                + ==- = + =   = =+= =   =       ++--          +     =   = = =--= ==++==
                =     ==- ==+-- = = = ++= +=--      ==+ ==--= +--+=-= ==- ==   =+=    =
                -               = = = =   +  +  ==+ = = +   =        ++    =          -
                -               = + + =   +  -  = + = = +   =        +     =          -
                --==++++==+=+++-= =-= =-+-=  =+-= =-= =--   +=++=+++==     -=+=++==+++-""";

        List<String> trackLines = Strings.splitByRow(track);

        int numberOfRows = trackLines.size();
        int numberOfColumns = trackLines.stream().mapToInt(String::length).max().orElseThrow();

        char[][] trackChars = new char[numberOfRows][numberOfColumns];

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                trackChars[rowIndex][columnIndex] = ' ';
            }
        }

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            String line = trackLines.get(rowIndex);
            char[] lineChars = line.toCharArray();
            System.arraycopy(lineChars, 0, trackChars[rowIndex], 0, lineChars.length);
        }

        List<TrackAction> trackActions = new ArrayList<>();

        ImmutableCell2D start = ImmutableCell2D.of(0, 0); // starting S
        ImmutableCell2D previousPosition = start;
        ImmutableCell2D currentPosition = ImmutableCell2D.of(0, 1); // cell to the right of the starting S

        while (!currentPosition.equals(start)) {

            // Add the action corresponding to the current symbol
            char currentChar = trackChars[currentPosition.getRow()][currentPosition.getColumn()];
            trackActions.add(TrackAction.fromChar(currentChar));

            // Move to the next position
            for (CardinalDirection2D direction: CardinalDirection2D.values()) {
                ImmutableCell2D possibleNextPosition = currentPosition.add(direction);

                if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, possibleNextPosition.getRow(), possibleNextPosition.getColumn())
                    && !possibleNextPosition.equals(previousPosition) && trackChars[possibleNextPosition.getRow()][possibleNextPosition.getColumn()] != ' ') {
                    previousPosition = currentPosition;
                    currentPosition = possibleNextPosition;
                    break;
                }

            }

        }

        trackActions.add(END);

        ListMultimap<String, Action> planIdToActions = createPlanActions(inputLines);
        Map.Entry<String, Collection<Action>> entry = planIdToActions.asMap().entrySet().iterator().next();
        List<Action> opponentPlanActions = (List<Action>) entry.getValue();

        long opponentTotalEssence = executePlan(opponentPlanActions, trackActions, 2024);

        List<Action> playerActions = new ArrayList<>(Collections.nCopies(5, PLUS));
        playerActions.addAll(Collections.nCopies(3, MINUS));
        playerActions.addAll(Collections.nCopies(3, MAINTAIN));

        Set<List<Action>> distinctPlayerPlans = new HashSet<>(Collections2.permutations(playerActions));

        int countGT = 0;

        int playerPlanCount = 0;

        for (List<Action> playerPlan: distinctPlayerPlans) {

            playerPlanCount++;

            long playerTotalEssence = executePlan(playerPlan, trackActions, 2024);

            if (playerTotalEssence > opponentTotalEssence) {
                countGT++;
            }

            if (playerPlanCount % 100 == 0) {
                log("Executing plan #{} / {}: {}. Essence gathered by player: {}", playerPlanCount, distinctPlayerPlans.size(), playerPlan, playerTotalEssence);
            }

        }

        return Integer.toString(countGT);
    }

}
