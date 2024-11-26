package challenges.ec2024;

import challenges.Quest;
import challenges.interfaces.ECEvent2024;
import challenges.interfaces.Quest07;
import challenges.params.ExecutionParameters;
import challenges.params.GenericExecutionParameter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;
import common.Grids;
import common.Strings;
import common.geo.CardinalDirection2D;
import common.geo.ImmutableCell2D;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

import static challenges.ec2024.EC2024Quest07.Action.*;
import static challenges.ec2024.EC2024Quest07.TrackAction.*;

public class EC2024Quest07 extends Quest implements ECEvent2024, Quest07 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ListMultimap<String, Action> planIdToActions = createPlanActions(inputLines);

        Map<String, Long> totalEssences = new HashMap<>();

        for (Map.Entry<String, Collection<Action>> entry: planIdToActions.asMap().entrySet()) {

            String planId = entry.getKey();
            List<Action> actions = (List<Action>) entry.getValue();
            int numberOfActions = actions.size();

            int currentValue = 10; // Start value

            List<Integer> values = new ArrayList<>();

            for (int segment = 0; segment < 10; segment++) {
                Action currentAction = actions.get( segment % numberOfActions );
                currentValue = currentAction.applyAsInt(currentValue);
                values.add(currentValue);
            }

            long totalEssence = values.stream().mapToInt(i -> i).sum();
            totalEssences.put(planId, totalEssence);

        }

        return getRankings(totalEssences);

    }


    public enum Action {

        PLUS(operand -> operand + 1),
        MINUS(operand -> Integer.max(operand - 1, 0)),
        MANTAIN(IntUnaryOperator.identity());

        private final IntUnaryOperator operator;

        Action(IntUnaryOperator operator) {
            this.operator = operator;
        }

        public int applyAsInt(int operand) {
            return operator.applyAsInt(operand);
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
                    case '=' -> MANTAIN;
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
        if (executionParameters instanceof GenericExecutionParameter<?> genericExecutionParameter) {
            track = (String) genericExecutionParameter.value();
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

        }

        return getRankings(totalEssences);
    }

    private static long executePlan(List<Action> planActions, List<TrackAction> trackActions, int numberOfTrackLoops) {

        int numberOfActions = planActions.size();

        int trackLength = trackActions.size();

        int currentValue = 10;
        int totalNumberOfSteps = 0;
        long total = 0;

        for (int trackLoopNumber = 1; trackLoopNumber <= numberOfTrackLoops; trackLoopNumber++) {

            for (int trackSegment = 0; trackSegment < trackLength; trackSegment++) {

                TrackAction trackAction = trackActions.get(totalNumberOfSteps % trackLength);
                Action planAction = planActions.get(totalNumberOfSteps % numberOfActions);

                Action actualAction = trackAction.transform(planAction);

                currentValue = actualAction.applyAsInt(currentValue);

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

        FORCED_PLUS(_ -> PLUS),
        FORCED_MINUS(_ -> MINUS),
        UNALTERED(UnaryOperator.identity()),
        END(UnaryOperator.identity());

        private final UnaryOperator<Action> operatorTransformator;

        TrackAction(UnaryOperator<Action> operatorTransformator) {
            this.operatorTransformator = operatorTransformator;
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

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

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
        Map.Entry<String, Action> entry = planIdToActions.entries().iterator().next();
        List<Action> opponentPlanActions = Collections.singletonList(entry.getValue());

        long opponentTotalEssence = executePlan(opponentPlanActions, trackActions, 2024);

        log("Opponent has total essence {}", opponentTotalEssence);

        List<Action> playerActions = new ArrayList<>(Collections.nCopies(5, PLUS));
        playerActions.addAll(Collections.nCopies(3, MINUS));
        playerActions.addAll(Collections.nCopies(3, MANTAIN));

        Set<List<Action>> distinctPlayerPlans = new HashSet<>(Collections2.permutations(playerActions));

        log("There are {} distinct player plans", distinctPlayerPlans.size());

        int countGT = 0;
        int countGE = 0;

        for (List<Action> playerPlan: distinctPlayerPlans) {

            long playerTotalEssence = executePlan(playerPlan, trackActions, 2024);

            if (playerTotalEssence >= opponentTotalEssence) {
                countGE++;
                if (playerTotalEssence > opponentTotalEssence) {
                    countGT++;
                }
            }

        }

        log("There are {} plans with essence >= than the opponent", countGE);
        log("There are {} plans with essence > than the opponent", countGT);

        return NOT_IMPLEMENTED;
    }


}
