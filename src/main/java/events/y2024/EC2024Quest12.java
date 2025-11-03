package events.y2024;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import common.AbstractQuest;
import common.geo.*;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest12;
import common.support.params.ExecutionParameters;

import java.util.*;

import static common.geo.ImmutableCoordinate2D.nTimes;
import static common.geo.PrincipalDirection2D.*;

public class EC2024Quest12 extends AbstractQuest implements MainEvent2024, Quest12 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int height = inputLines.size();

        List<Character> sources = List.of('A', 'B', 'C');

        Map<Character, Integer> sourceValues = Map.of('A', 1, 'B', 2, 'C', 3);
        Map<Character, ImmutableCoordinate2D> sourcePositions = new HashMap<>();
        NavigableSet<ImmutableCoordinate2D> targetPositions = new TreeSet<>();

        for (int inputLineRowIndex = 0; inputLineRowIndex < height; inputLineRowIndex++) {

            String line = inputLines.get(inputLineRowIndex);

            int width = line.length();

            for (int columnIndex = 0; columnIndex < width; columnIndex++) {
                char c = line.charAt(columnIndex);

                if (c == 'A' || c == 'B' || c == 'C') {
                    sourcePositions.put(c, ImmutableCoordinate2D.of(columnIndex, height - inputLineRowIndex - 2));
                } else if (c == 'T') {
                    targetPositions.add(ImmutableCoordinate2D.of(columnIndex, height - inputLineRowIndex - 2));
                }
            }

        }

        int total = 0;

        while (!targetPositions.isEmpty()) {

            ImmutableCoordinate2D targetTowerTopPosition = targetPositions.getFirst();
            int targetTowerHeight = targetTowerTopPosition.getY();

            for (int step = 0; step <= targetTowerHeight; step++ ) {

                ImmutableCoordinate2D targetPosition = targetTowerTopPosition.add(nTimes(SOUTH, step));

                if (targetPositions.remove(targetPosition)) {

                    int rankingValue = computeRankingValue(sources, sourceValues, sourcePositions, targetPosition);
                    total += rankingValue;

                }

            }

        }

        return Integer.toString(total);
    }

    private List<ImmutableCoordinate2D> createPath(ImmutableCoordinate2D source, int shootingPower) {

        List<ImmutableCoordinate2D> path = new ArrayList<>();

        MutableCoordinate2D projectileCurrentPosition = MutableCoordinate2D.copyOf(source);

        // Move up and to the right
        for (int step = 1; step <= shootingPower; step++) {
            projectileCurrentPosition.move(NORTH_EAST);
            path.add(ImmutableCoordinate2D.copyOf(projectileCurrentPosition));
        }

        // To the right
        for (int step = 1; step <= shootingPower; step++) {
            projectileCurrentPosition.move(EAST);
            path.add(ImmutableCoordinate2D.copyOf(projectileCurrentPosition));
        }

        int heightReached = projectileCurrentPosition.getY();
        // Move down
        for (int step = 1; step <= heightReached; step++) {
            projectileCurrentPosition.move(SOUTH_EAST);
            path.add(ImmutableCoordinate2D.copyOf(projectileCurrentPosition));
        }

        return path;

    }

    private int computeRankingValue(List<Character> sources, Map<Character, Integer> sourceValues,
                                    Map<Character, ImmutableCoordinate2D> sourcePositions, ImmutableCoordinate2D targetPosition) {

        int distanceToTarget = targetPosition.getX();
        for (int shootingPower = 1; shootingPower < distanceToTarget; shootingPower++) {
            for (Character source: sources) {

                ImmutableCoordinate2D sourcePosition = sourcePositions.get(source);
                List<ImmutableCoordinate2D> path = createPath(sourcePosition, shootingPower);

                if (path.contains(targetPosition)) {
                    return sourceValues.get(source) * shootingPower;
                }

            }

        }

        throw new IllegalStateException("No trajectory hits the target");

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int height = inputLines.size();

        List<Character> sources = List.of('A', 'B', 'C');

        Map<Character, Integer> sourceValues = Map.of('A', 1, 'B', 2, 'C', 3);
        Map<Character, ImmutableCoordinate2D> sourcePositions = new HashMap<>();
        NavigableSet<ImmutableCoordinate2D> targetPositions = new TreeSet<>();
        Set<ImmutableCoordinate2D> hardRockPositions = new HashSet<>();

        for (int inputLineRowIndex = 0; inputLineRowIndex < height; inputLineRowIndex++) {

            String line = inputLines.get(inputLineRowIndex);

            int width = line.length();

            for (int columnIndex = 0; columnIndex < width; columnIndex++) {
                char c = line.charAt(columnIndex);

                if (c == 'A' || c == 'B' || c == 'C') {
                    sourcePositions.put(c, ImmutableCoordinate2D.of(columnIndex, height - inputLineRowIndex - 2));
                } else if (c == 'T' || c == 'H') {

                    ImmutableCoordinate2D coordinate2D = ImmutableCoordinate2D.of(columnIndex, height - inputLineRowIndex - 2);
                    targetPositions.add(coordinate2D);
                    if (c == 'H') {
                        hardRockPositions.add(coordinate2D);
                    }

                }
            }

        }

        int total = 0;

        while (!targetPositions.isEmpty()) {

            ImmutableCoordinate2D targetTowerTopPosition = targetPositions.getFirst();
            int targetTowerHeight = targetTowerTopPosition.getY();

            for (int step = 0; step <= targetTowerHeight; step++ ) {

                ImmutableCoordinate2D targetPosition = targetTowerTopPosition.add(nTimes(SOUTH, step));

                if (targetPositions.remove(targetPosition)) {

                    int rankingValue = computeRankingValue(sources, sourceValues, sourcePositions, targetPosition);

                    if (hardRockPositions.contains(targetPosition)) {
                        rankingValue *= 2;
                    }

                    total += rankingValue;

                }

            }

        }

        return Integer.toString(total);


    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ImmutableCoordinate2D[] sourcePositions = {
                ImmutableCoordinate2D.of(0, 0),
                ImmutableCoordinate2D.of(0, 1),
                ImmutableCoordinate2D.of(0, 2)
        };

        long total = 0;

        for (String line: inputLines) {
            String[] parts = line.split(" ");
            int startingX = Integer.parseInt(parts[0]);
            int startingY = Integer.parseInt(parts[1]);

            long rankingValue = minRankingValue(sourcePositions, startingX, startingY);
            total += rankingValue;

        }

        return Long.toString(total);
    }

    private long minRankingValue(ImmutableCoordinate2D[] sourcePositions, int startingX, int startingY) {

        ImmutableCoordinate2D startingPosition = ImmutableCoordinate2D.of(startingX, startingY);

        int meteorMaxFlightTime = Integer.min(startingX, startingY);
        int minXReachableByMeteor = startingX - meteorMaxFlightTime;

        //log("Meteor that starts at coordinate {} has max flight time of {}, min reachable x is {}",
                //startingPosition, meteorMaxFlightTime, minXReachableByMeteor);

        int minDelay = startingX % 2;
        int maxDelay = 2 * meteorMaxFlightTime - startingX;

        for (int delay = minDelay; delay <= maxDelay; delay += 2) {

            int flightTime = (startingX - delay) / 2;
            int meetingTime = delay + flightTime;
            ImmutableCoordinate2D meteorMeetingPosition = startingPosition.add(nTimes(SOUTH_WEST, meetingTime));

            //log("Delay {}, flight time {}, meeting time {}. At that time, meteor is at coordinate {}", delay, flightTime, meetingTime, meteorMeetingPosition);

            OptionalLong optionalRankingValue = minRankingValueForThisDelay(sourcePositions, flightTime, meteorMeetingPosition);

            if (optionalRankingValue.isPresent()) {
                return optionalRankingValue.getAsLong();
            }

        }

        throw new IllegalStateException("Should not get here");

    }

    private OptionalLong minRankingValueForThisDelay(ImmutableCoordinate2D[] sourcePositions, int flightTime, ImmutableCoordinate2D meteorMeetingPosition) {

        /*
            For all values of shooting power >= flightTime, the final position of the projectile stays the same.
            If it misses the meteor by then, it will miss for all shooting powers.

            The max ranking value that we need to check is 3 * flightTime, because for this ranking value we have shootingPower = flightTime.

         */

        for (int rankingValue = 1; rankingValue <= 3 * flightTime; rankingValue++) {

            //log("Checking ranking value {}", rankingValue);

            for (int towerSegment = 1; towerSegment <= 3; towerSegment++) {

                if (rankingValue % towerSegment == 0) {

                    // It is possible to obtain this ranking value by shooting from this tower segment
                    int shootingPower = rankingValue / towerSegment;

                    if (shootingPower <= flightTime) {

                        //log("Shooting from tower segment {} with shooting power {}", towerSegment, shootingPower);

                        Optional<ImmutableCoordinate2D> position = positionInTrajectory(sourcePositions[towerSegment - 1], shootingPower, flightTime);

                        if (position.isPresent() && position.get().equals(meteorMeetingPosition)) {
                            //log("METEOR DESTROYED! Ranking value {}", rankingValue);
                            return OptionalLong.of(rankingValue);
                        } else {
                            //log("Missed");
                        }

                    }

                }

            }

        }

        return OptionalLong.empty();

    }

    private Optional<ImmutableCoordinate2D> positionInTrajectory(ImmutableCoordinate2D source, int shootingPower, int flightTime) {

        if (flightTime <= shootingPower) {
            return Optional.of(source.add( nTimes(NORTH_EAST, flightTime) ) );
        }

        if (flightTime <= shootingPower * 2) {
            return Optional.of(source
                    .add(nTimes(NORTH_EAST, shootingPower))
                    .add(nTimes(EAST, flightTime - shootingPower))
            );
        }

        if (flightTime <= shootingPower * 3 + source.getY()) {
            return Optional.of(source
                    .add(nTimes(NORTH_EAST, shootingPower))
                    .add(nTimes(EAST, shootingPower))
                    .add(nTimes(SOUTH_EAST, flightTime - 2 * shootingPower))
            );
        }

        return Optional.empty();

    }

}
