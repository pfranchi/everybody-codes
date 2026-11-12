package events.y2026;

import common.AbstractQuest;
import common.Sections;
import common.support.interfaces.MainEvent2026;
import common.support.interfaces.Quest13;
import common.support.params.ExecutionParameters;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EC2026Quest13 extends AbstractQuest implements MainEvent2026, Quest13 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(input);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(input);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solve(input);
    }

    private String solve(String input) {

        return Sections
                .splitAtBlankLines(input)
                .stream()
                .map(this::solveSingleClock)
                .reduce(BigInteger.ZERO, BigInteger::add)
                .toString();

    }

    private static final Pattern PATTERN = Pattern.compile("""
            ^hands (?<hands>\\d+(?::\\d+)*)
            direction (?<directions>(?:CW|CCW)(?::(?:CW|CCW))*)
            current (?<current>\\d+(?::\\d+)*)
            move (?<moveTimes>\\d+(?::\\d+)*)$""");

    private BigInteger solveSingleClock(List<String> singleClockSection) {

        Matcher matcher = PATTERN.matcher(String.join("\n", singleClockSection));

        if (!matcher.matches()) {
            throw new IllegalArgumentException();
        }

        String[] line1Parts = matcher.group("hands").split(":");
        String[] line2Parts = matcher.group("directions").split(":");
        String[] line3Parts = matcher.group("current").split(":");
        String[] line4Parts = matcher.group("moveTimes").split(":");
        int numberOfHands = line1Parts.length;

        List<BigInteger> numbersOfPositions = new ArrayList<>();
        List<RotationDirection> rotationDirections = new ArrayList<>();
        List<BigInteger> startingValues = new ArrayList<>();
        List<BigInteger> moveTimes = new ArrayList<>();

        for (int handIndex = 0; handIndex < numberOfHands; handIndex++) {

            BigInteger numberOfPositions = new BigInteger(line1Parts[handIndex]);

            numbersOfPositions.add(numberOfPositions);
            rotationDirections.add(RotationDirection.of(line2Parts[handIndex]));
            BigInteger startingValue = new BigInteger(line3Parts[handIndex]);
            startingValues.add(startingValue);
            moveTimes.add(new BigInteger(line4Parts[handIndex]));

        }

        BigInteger lcm = numbersOfPositions.stream().reduce((a, b) -> a.multiply(b).divide(a.gcd(b))).orElseThrow();

        List<Hand> hands = new ArrayList<>();

        for (int handIndex = 0; handIndex < numberOfHands; handIndex++) {

            BigInteger numberOfPositions = numbersOfPositions.get(handIndex);
            RotationDirection rotationDirection = rotationDirections.get(handIndex);
            BigInteger startingValue = startingValues.get(handIndex);
            BigInteger moveTime = moveTimes.get(handIndex);
            BigInteger coefficient = lcm.divide(numberOfPositions);
            BigInteger increment = rotationDirection == RotationDirection.CLOCKWISE ? coefficient : coefficient.multiply(BigInteger.valueOf(-1));
            BigInteger adjustedStartingValue = startingValue.multiply(coefficient);

            Hand hand = new Hand(moveTime, increment, adjustedStartingValue);

            hands.add(hand);

        }

        BigInteger total =  BigInteger.ZERO;
        for (int hand1Index = 0; hand1Index < numberOfHands - 1; hand1Index++) {
            for (int hand2Index = hand1Index + 1; hand2Index < numberOfHands; hand2Index++) {

                Hand hand1 = hands.get(hand1Index);
                Hand hand2 = hands.get(hand2Index);

                BigInteger hand1MoveTime = hand1.moveTime();
                BigInteger hand2MoveTime = hand2.moveTime();

                BigInteger minMoveTime = hand1MoveTime.min(hand2MoveTime);

                BigInteger meetingsInPhase1 = countMeetingsEfficient(lcm, hand1, hand2, BigInteger.ZERO, minMoveTime);
                BigInteger meetingsInPhase2 = countMeetingsEfficient(lcm, hand1, hand2, minMoveTime, hand1MoveTime.max(hand2MoveTime));

                total = total.add(meetingsInPhase1).add(meetingsInPhase2);

            }
        }

        return total;

    }

    private record Hand(BigInteger moveTime, BigInteger increment, BigInteger adjustedStartingValue) {

        public BigInteger absolutePositionAt(BigInteger time) {
            BigInteger actualMoveTime = moveTime.min(time);
            BigInteger movement = increment.multiply(actualMoveTime);
            return adjustedStartingValue.add(movement);
        }

    }

    private static BigInteger modInRange(BigInteger x, BigInteger mod) {
        return (x.mod(mod).add(mod)).mod(mod);
    }

    private static BigInteger floorDiv(BigInteger a, BigInteger b) {
        BigInteger[] qr = a.divideAndRemainder(b);
        return a.signum() != b.signum() && !qr[1].equals(BigInteger.ZERO) ? qr[0].subtract(BigInteger.ONE) : qr[0];
    }

    private BigInteger countMeetingsEfficient(BigInteger lcm, Hand hand1, Hand hand2, BigInteger startTime, BigInteger endTime) {

        Map<Key, State> cache = new HashMap<>();

        BigInteger hand1AbsolutePositionAtStartTime = hand1.absolutePositionAt(startTime);
        BigInteger hand2AbsolutePositionAtStartTime = hand2.absolutePositionAt(startTime);
        BigInteger deltaAtStartTime = hand1AbsolutePositionAtStartTime.subtract(hand2AbsolutePositionAtStartTime);
        BigInteger quotientAtStartTime = floorDiv(deltaAtStartTime, lcm);
        BigInteger hand1ModdedPositionAtStartTime = modInRange(hand1AbsolutePositionAtStartTime, lcm);
        BigInteger hand2ModdedPositionAtStartTime = modInRange(hand2AbsolutePositionAtStartTime, lcm);
        boolean overlappingAtStartTime = hand1ModdedPositionAtStartTime.equals(hand2ModdedPositionAtStartTime);

        BigInteger meetingCount = BigInteger.ZERO;

        Key keyAtStartTime = new Key(hand1ModdedPositionAtStartTime, hand2ModdedPositionAtStartTime);
        State stateAtStartTime = new State(startTime, meetingCount);
        cache.put(keyAtStartTime, stateAtStartTime);

        BigInteger previousDeltaQuotient = quotientAtStartTime;
        boolean previouslyOverlapping = overlappingAtStartTime;

        BigInteger time = startTime.add(BigInteger.ONE);
        boolean checkInCache = true;
        while (time.compareTo(endTime) <= 0) {

            BigInteger hand1AbsolutePosition = hand1.absolutePositionAt(time);
            BigInteger hand2AbsolutePosition = hand2.absolutePositionAt(time);
            BigInteger hand1ModdedPosition = modInRange(hand1AbsolutePosition, lcm);
            BigInteger hand2ModdedPosition = modInRange(hand2AbsolutePosition, lcm);
            BigInteger delta = hand1AbsolutePosition.subtract(hand2AbsolutePosition);
            BigInteger newDeltaQuotient = floorDiv(delta, lcm);
            boolean currentlyOverlapping = modInRange(hand1AbsolutePosition, lcm).equals(modInRange(hand2AbsolutePosition, lcm));

            Key key = new Key(hand1ModdedPosition, hand2ModdedPosition);

            if (currentlyOverlapping) {
                if (!previouslyOverlapping) {
                    meetingCount = meetingCount.add(BigInteger.ONE);
                }
            } else {

                BigInteger meetings = (newDeltaQuotient.subtract(previousDeltaQuotient)).abs();

                if (previouslyOverlapping) {
                    meetings = (meetings.subtract(BigInteger.ONE)).max(BigInteger.ZERO);
                }

                meetingCount = meetingCount.add(meetings);

            }

            State currentState = new State(time, meetingCount);

            if (checkInCache && cache.containsKey(key)) {

                State oldState = cache.get(key);
                checkInCache = false;

                BigInteger timeOffset = oldState.time();
                BigInteger timeDelta = currentState.time().subtract(oldState.time());

                BigInteger meetingCountDelta = currentState.meetingCount().subtract(oldState.meetingCount());

                BigInteger[] quotientAndRemainder = endTime.subtract(timeOffset).divideAndRemainder(timeDelta);
                BigInteger quotient = quotientAndRemainder[0];
                BigInteger repetitions = quotient.subtract(BigInteger.ONE);
                BigInteger fullTimeDelta = timeDelta.multiply(repetitions);
                BigInteger reachedTime = time.add(fullTimeDelta);

                BigInteger hand1PositionAfterSkip = hand1.absolutePositionAt(reachedTime);
                BigInteger hand2PositionAfterSkip = hand2.absolutePositionAt(reachedTime);
                BigInteger hand1ModdedPositionAfterSkip = modInRange(hand1PositionAfterSkip, lcm);
                BigInteger hand2ModdedPositionAfterSkip = modInRange(hand2PositionAfterSkip, lcm);
                BigInteger deltaAfterSkip = hand1PositionAfterSkip.subtract(hand2PositionAfterSkip);
                BigInteger newDeltaQuotientAfterSkip = floorDiv(deltaAfterSkip, lcm);
                boolean currentlyOverlappingAfterSkip = hand1ModdedPositionAfterSkip.equals(hand2ModdedPositionAfterSkip);

                // Skip time
                time = reachedTime;
                meetingCount = meetingCount.add(meetingCountDelta.multiply(repetitions));
                previousDeltaQuotient = newDeltaQuotientAfterSkip;
                previouslyOverlapping = currentlyOverlappingAfterSkip;

            } else {

                cache.put(key, currentState);

                previousDeltaQuotient = newDeltaQuotient;
                previouslyOverlapping = currentlyOverlapping;

            }

            time = time.add(BigInteger.ONE);

        }

        return meetingCount;

    }

    private record Key(BigInteger hand1ModdedPosition, BigInteger hand2ModdedPosition) {}

    private record State(BigInteger time, BigInteger meetingCount) {}

    private enum RotationDirection {
        CLOCKWISE, COUNTER_CLOCKWISE;

        public static RotationDirection of(String s) {
            return switch (s) {
                case "CW" -> RotationDirection.CLOCKWISE;
                case "CCW" -> RotationDirection.COUNTER_CLOCKWISE;
                default -> throw new IllegalArgumentException("Unexpected value: " + s);
            };
        }
    }

}
