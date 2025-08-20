package events.y2024;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import common.AbstractQuest;
import common.geo.CardinalDirection3D;
import common.geo.ImmutableCoordinate3D;
import common.geo.MutableCoordinate3D;
import common.pathfinding.PathFinder;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest14;
import common.support.params.ExecutionParameters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EC2024Quest14 extends AbstractQuest implements MainEvent2024, Quest14 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        MutableCoordinate3D coordinate = MutableCoordinate3D.atOrigin();

        int maxZSeen = Integer.MIN_VALUE;

        for (String instruction: input.split(",")) {
            char firstChar = instruction.charAt(0);
            int numberOfSteps = Integer.parseInt(instruction.substring(1));

            CardinalDirection3D direction = switch (firstChar) {
                case 'U' -> CardinalDirection3D.UP;
                case 'D' -> CardinalDirection3D.DOWN;
                case 'R' -> CardinalDirection3D.RIGHT;
                case 'L' -> CardinalDirection3D.LEFT;
                case 'F' -> CardinalDirection3D.FORWARD;
                case 'B' -> CardinalDirection3D.BACKWARD;
                default -> throw new IllegalArgumentException("Unexpected char: " + firstChar);
            };

            coordinate.move(ImmutableCoordinate3D.nTimes(direction, numberOfSteps));

            maxZSeen = Integer.max(maxZSeen, coordinate.getZ());

        }

        return Integer.toString(maxZSeen);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Set<ImmutableCoordinate3D> uniquePlantSegments = new HashSet<>();

        for (String inputLine: inputLines) {

            MutableCoordinate3D coordinate = MutableCoordinate3D.atOrigin();

            for (String instruction: inputLine.split(",")) {
                char firstChar = instruction.charAt(0);
                int numberOfSteps = Integer.parseInt(instruction.substring(1));

                CardinalDirection3D direction = switch (firstChar) {
                    case 'U' -> CardinalDirection3D.UP;
                    case 'D' -> CardinalDirection3D.DOWN;
                    case 'R' -> CardinalDirection3D.RIGHT;
                    case 'L' -> CardinalDirection3D.LEFT;
                    case 'F' -> CardinalDirection3D.FORWARD;
                    case 'B' -> CardinalDirection3D.BACKWARD;
                    default -> throw new IllegalArgumentException("Unexpected char: " + firstChar);
                };

                for (int step = 1; step <= numberOfSteps; step++) {

                    coordinate.move(direction);
                    uniquePlantSegments.add(ImmutableCoordinate3D.copyOf(coordinate));

                }

            }

        }

        return Integer.toString(uniquePlantSegments.size());
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Set<ImmutableCoordinate3D> uniquePlantSegments = new HashSet<>();
        List<ImmutableCoordinate3D> leaves = new ArrayList<>();

        for (String inputLine: inputLines) {

            MutableCoordinate3D coordinate = MutableCoordinate3D.atOrigin();

            for (String instruction: inputLine.split(",")) {
                char firstChar = instruction.charAt(0);
                int numberOfSteps = Integer.parseInt(instruction.substring(1));

                CardinalDirection3D direction = switch (firstChar) {
                    case 'U' -> CardinalDirection3D.UP;
                    case 'D' -> CardinalDirection3D.DOWN;
                    case 'R' -> CardinalDirection3D.RIGHT;
                    case 'L' -> CardinalDirection3D.LEFT;
                    case 'F' -> CardinalDirection3D.FORWARD;
                    case 'B' -> CardinalDirection3D.BACKWARD;
                    default -> throw new IllegalArgumentException("Unexpected char: " + firstChar);
                };

                for (int step = 1; step <= numberOfSteps; step++) {

                    coordinate.move(direction);
                    uniquePlantSegments.add(ImmutableCoordinate3D.copyOf(coordinate));

                }

            }

            // Add the leaf to the list
            leaves.add(ImmutableCoordinate3D.copyOf(coordinate));

        }

        // Create graph of connected plant segments

        ImmutableGraph.Builder<ImmutableCoordinate3D> graphBuilder = GraphBuilder.undirected().immutable();

        for (ImmutableCoordinate3D plantSegment: uniquePlantSegments) {

            for (CardinalDirection3D direction3D: CardinalDirection3D.values()) {
                ImmutableCoordinate3D neighbor = plantSegment.add(direction3D);
                if (uniquePlantSegments.contains(neighbor)) {
                    graphBuilder.putEdge(plantSegment, neighbor);
                }
            }

        }

        ImmutableGraph<ImmutableCoordinate3D> graph = graphBuilder.build();

        PathFinder<ImmutableCoordinate3D> pathFinder = PathFinder.forUnweightedGraph(graph);

        int minTotalDistance = Integer.MAX_VALUE;

        for (ImmutableCoordinate3D trunkSegment: uniquePlantSegments) {
            if (trunkSegment.getX() == 0 && trunkSegment.getY() == 0) {

                int totalDistance = 0;

                for (ImmutableCoordinate3D leaf: leaves) {
                    totalDistance += pathFinder.shortestDistance(trunkSegment, leaf);
                }

                minTotalDistance = Integer.min(minTotalDistance, totalDistance);

            }
        }

        return Integer.toString(minTotalDistance);
    }
}
