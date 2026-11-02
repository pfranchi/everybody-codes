package events.y2026;

import common.AbstractQuest;
import common.support.interfaces.MainEvent2026;
import common.support.interfaces.Quest04;
import common.support.params.ExecutionParameters;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EC2026Quest04 extends AbstractQuest implements MainEvent2026, Quest04 {

    private static final Pattern PATTERN = Pattern.compile(
            "^id=(?<id>\\d+) quest=(?<quest>\\d+) part=(?<part>[123]) time=(?<time>\\d+)$");

    private record ParsedLine(int id, int quest, int part, int time) {

        public static ParsedLine parse(String line) {
            Matcher matcher = PATTERN.matcher(line);
            if (matcher.matches()) {
                int id = Integer.parseInt(matcher.group("id"));
                int quest = Integer.parseInt(matcher.group("quest"));
                int part = Integer.parseInt(matcher.group("part"));
                int time = Integer.parseInt(matcher.group("time"));
                return new ParsedLine(id, quest, part, time);
            }
            throw new IllegalArgumentException();
        }

    }

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Map<Integer, Integer> totalTimesById = inputLines
                .stream()
                .map(ParsedLine::parse)
                .collect(Collectors.toMap(ParsedLine::id, ParsedLine::time, Integer::sum));

        List<Integer> sortedTotalTimes = totalTimesById
                .values()
                .stream()
                .sorted()
                .toList();

        long total = IntStream
                .range(0, sortedTotalTimes.size())
                .mapToLong(i -> (i + 1) * (long) sortedTotalTimes.get(i))
                .sum();

        return Long.toString(total);
    }

    private record NumberOfPartsAndTotalTime(int numberOfParts, int totalTime) implements Comparable<NumberOfPartsAndTotalTime> {

        public NumberOfPartsAndTotalTime(ParsedLine parsedLine) {
            this(1, parsedLine.time());
        }

        public NumberOfPartsAndTotalTime combine(NumberOfPartsAndTotalTime other) {
            return new NumberOfPartsAndTotalTime(
                    this.numberOfParts + other.numberOfParts, this.totalTime + other.totalTime
            );
        }

        @Override
        public int compareTo(@Nonnull NumberOfPartsAndTotalTime o) {
            return Comparator.comparingInt(NumberOfPartsAndTotalTime::numberOfParts).reversed()
                    .thenComparingInt(NumberOfPartsAndTotalTime::totalTime)
                    .compare(this, o);
        }
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Map<Integer, NumberOfPartsAndTotalTime> numberOfPartsAndTotalTimeMap = inputLines.stream()
                .map(ParsedLine::parse)
                .collect(Collectors.toMap(ParsedLine::id, NumberOfPartsAndTotalTime::new, NumberOfPartsAndTotalTime::combine));

        List<NumberOfPartsAndTotalTime> sortedList = numberOfPartsAndTotalTimeMap
                .values()
                .stream()
                .sorted()
                .toList();

        long total = IntStream
                .range(0, sortedList.size())
                .mapToLong(i -> (i + 1) * (long) sortedList.get(i).totalTime())
                .sum();

        return Long.toString(total);

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        NavigableSet<Integer> distinctParticipantIds = new TreeSet<>();
        NavigableSet<Integer> distinctQuestIds = new TreeSet<>();

        NavigableMap<Key, Integer> timeBySolvedPart = new TreeMap<>();

        for (String line: inputLines) {
            ParsedLine parsedLine = ParsedLine.parse(line);

            distinctParticipantIds.add(parsedLine.id());
            distinctQuestIds.add(parsedLine.quest());

            timeBySolvedPart.put(new Key(parsedLine.id(), parsedLine.quest(), parsedLine.part()), parsedLine.time());

        }

        Map<Integer, Integer> totalPointsMap = distinctParticipantIds
                .stream()
                .mapToInt(participantId -> participantId)
                .boxed()
                .collect(Collectors.toMap(participantId -> participantId, _ -> 0, (a, b) -> b));

        NavigableMap<Key, Integer> totalTimesBySolvedPart = new TreeMap<>();
        for (Key key: timeBySolvedPart.keySet()) {

            int totalTime = 0;
            for (int part = 1; part <= key.part(); part++) {
                Key keyOfPreviousPart = new Key(key.participantId(), key.quest(), part);

                Integer timeOfSinglePart = timeBySolvedPart.get(keyOfPreviousPart);
                if (timeOfSinglePart == null) {
                    throw new IllegalStateException("Must have solved the previous part. Key = " + key);
                }

                totalTime += timeOfSinglePart;

            }

            totalTimesBySolvedPart.merge(key, totalTime, Integer::sum);

        }

        for (int questId: distinctQuestIds) {

            for (int part = 1; part <= 3; part++) {

                // Participant ids and times, sorted by smallest time first

                PriorityQueue<ParticipantIdAndTime> participantIdAndTimes = new PriorityQueue<>();

                for (Map.Entry<Key, Integer> entry: totalTimesBySolvedPart.entrySet()) {

                    Key key = entry.getKey();
                    if (key.quest() == questId && key.part() == part) {

                        // This participant solved this part and is therefore eligible for some
                        // points, depending on the results of the other participants
                        participantIdAndTimes.add(new ParticipantIdAndTime(key.participantId(), entry.getValue()));

                    }

                }

                // Assign points

                int currentlyAwardedTime = -1;
                int currentlyAwardedPoints = 50 * part + 1;
                int streak = 1;

                // Do not iterate over the priority queue because it is not fully sorted, it only guarantees
                // that, at any moment, the head of the queue is the smallest element

                while (!participantIdAndTimes.isEmpty()) {

                    ParticipantIdAndTime participantIdAndTime = participantIdAndTimes.remove();

                    int currentParticipantId = participantIdAndTime.participantId();
                    int currentTime = participantIdAndTime.time();

                    if (currentTime == currentlyAwardedTime) {
                        // Continue the streak
                        totalPointsMap.merge(currentParticipantId, currentlyAwardedPoints, Integer::sum);
                        streak++;
                    } else {
                        // Reset the streak
                        currentlyAwardedPoints -= streak;
                        streak = 1;
                        currentlyAwardedTime = currentTime;

                        if (currentlyAwardedPoints >= 0) {
                            totalPointsMap.merge(currentParticipantId, currentlyAwardedPoints, Integer::sum);
                        } else {
                            break;
                        }

                    }

                }

            }

        }

        // Total points, sorted by highest points first
        List<Integer> listOfTotalPoints = totalPointsMap.values().stream().sorted(Comparator.reverseOrder()).toList();

        long checksum = 0;

        long checksumCoeff = 1;
        int numberOfPointsForCurrentStreak = -1;
        int streak = 0;

        for (int totalPoints: listOfTotalPoints) {

            if (totalPoints == numberOfPointsForCurrentStreak) {
                // Maintain the same coefficient and increment the streak
                streak++;
            } else {
                // Increase the coefficient by the streak length and reset the streak counter
                checksumCoeff += streak;
                streak = 1;
                numberOfPointsForCurrentStreak = totalPoints;
            }

            // Add to the checksum
            checksum += checksumCoeff * totalPoints;

        }

        return Long.toString(checksum);
    }

    private record ParticipantIdAndTime(int participantId, int time) implements Comparable<ParticipantIdAndTime> {

        @Override
        public int compareTo(@Nonnull ParticipantIdAndTime o) {
            return Comparator.comparingInt(ParticipantIdAndTime::time).compare(this, o);
        }
    }

    private record Key(int participantId, int quest, int part) implements Comparable<Key> {
        @Override
        public int compareTo(@Nonnull Key o) {
            return Comparator
                    .comparingInt(Key::quest)
                    .thenComparingInt(Key::part)
                    .thenComparingInt(Key::participantId)
                    .compare(this, o);
        }
    }

}
