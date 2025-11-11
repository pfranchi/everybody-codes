package events.y2025;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SortedSetMultimap;
import common.AbstractQuest;
import common.Strings;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest06;
import common.support.params.ExecutionParameters;
import common.support.params.GenericExecutionParameter;

import java.util.*;

public class EC2025Quest06 extends AbstractQuest implements MainEvent2025, Quest06 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfMentorsSeen = 0;
        int count = 0;

        for (char c: Strings.firstRow(input).toCharArray()) {
            if (c == 'A') {
                numberOfMentorsSeen++;
            }
            if (c == 'a') {
                count += numberOfMentorsSeen;
            }
        }

        return Integer.toString(count);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Map<Character, Long> mentorCounts = new HashMap<>();
        long count = 0;

        for (char c: Strings.firstRow(input).toCharArray()) {

            if (Character.isUpperCase(c)) {
                mentorCounts.merge(c, 1L, (oldValue, _) -> oldValue + 1);
            } else {
                count += mentorCounts.getOrDefault(Character.toUpperCase(c), 0L);
            }

        }

        return Long.toString(count);
    }

    record Part3Parameters(int distanceLimit, int patternRepeats) {}

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int distanceLimit = 1000;
        int patternRepeats = 1000;

        if (executionParameters instanceof GenericExecutionParameter<?>(Part3Parameters(int limit, int repeats))) {
            distanceLimit = limit;
            patternRepeats = repeats;
        }

        char[] chars = Strings.firstRow(input).toCharArray();
        long patternLength = chars.length;

        /*
            TODO Create only one implementation that always works

            Solution 2 does not work if distanceLimit > patternLength because it is possible that the view wraps around the pattern more than once.
            However the number of times that the view wraps around not only depends on the length of the overshoot but is also limited by the amount
            of repetitions actually available to the side. For example, if distanceLimit = 100 and patternLength = 2, a view would wrap around
            the pattern 50 times to the left and 50 times to the right. If there are 6 pattern repetitions, neither side can offer enough pattern
            repetitions for the view to include a full 50 pattern repetitions. If there are 4000 pattern repetition, "starting" and "middle" repetitions
            have enough pattern repetitions to the right to fully wrap around them, however the 3998th repetition does not have the full view to the right.

            If distanceLimit <= patternLength solution 2 can be used because in this case numberOfFullWrapArounds = 0, which
            means that only one repetition on either side is used (and not used completely).

            Solution 2 is much faster.

         */

        if (distanceLimit > patternLength) {
            return part3Solution1(patternRepeats, patternLength, chars, distanceLimit);
        }

        return part3Solution2(patternRepeats, (int) patternLength, chars, distanceLimit);

    }

    private String part3Solution1(int patternRepeats, long patternLength, char[] chars, int distanceLimit) {

        // First pass: mentors

        SortedSetMultimap<Character, Long> mentorPositionIndexes = MultimapBuilder.hashKeys()
                .treeSetValues().build();

        for (int repetition = 0; repetition < patternRepeats; repetition++) {

            for (int patternCharIndex = 0; patternCharIndex < patternLength; patternCharIndex++) {

                char c = chars[patternCharIndex];
                long globalIndex = repetition * patternLength + patternCharIndex;

                if (Character.isUpperCase(c)) {
                    mentorPositionIndexes.put(c, globalIndex);
                }

            }

        }

        // Second pass

        long total = 0L;

        for (int repetition = 0; repetition < patternRepeats; repetition++) {

            for (int patternCharIndex = 0; patternCharIndex < patternLength; patternCharIndex++) {

                char c = chars[patternCharIndex];
                long globalIndex = repetition * patternLength + patternCharIndex;

                if (Character.isLowerCase(c)) {

                    SortedSet<Long> allMentors = mentorPositionIndexes.get(Character.toUpperCase(c));
                    long mentorsSeen = allMentors.subSet(globalIndex - distanceLimit, globalIndex + distanceLimit + 1).size();
                    total += mentorsSeen;

                }

            }

        }

        return Long.toString(total);

    }

    private String part3Solution2(int patternRepeats, int patternLength, char[] chars, int distanceLimit) {

        SortedSetMultimap<Character, Integer> mentorPositionIndexes = MultimapBuilder.hashKeys()
                .treeSetValues().build();

        for (int patternCharIndex = 0; patternCharIndex < patternLength; patternCharIndex++) {

            char c = chars[patternCharIndex];

            if (Character.isUpperCase(c)) {
                mentorPositionIndexes.put(c, patternCharIndex);
            }

        }

        long total = 0L;

        if (patternRepeats == 1) {

            // No wrap around on either side

            for (int index = 0; index < patternLength; index++) {

                char novice = chars[index];

                if (Character.isLowerCase(novice)) {

                    int leftBoundaryInclusive = index - distanceLimit;
                    int rightBoundaryExclusive = index + distanceLimit + 1;

                    SortedSet<Integer> allMentors = mentorPositionIndexes.get(Character.toUpperCase(novice));

                    long mentorsSeen = allMentors.subSet(leftBoundaryInclusive, rightBoundaryExclusive).size();

                    total += mentorsSeen;

                }

            }

        } else {

            // Do a pass where the right view wraps around but the left view does not

            for (int index = 0; index < patternLength; index++) {

                char novice = chars[index];

                if (Character.isLowerCase(novice)) {

                    int leftBoundaryInclusive = index - distanceLimit;
                    int rightBoundaryExclusive = index + distanceLimit + 1;

                    SortedSet<Integer> allMentors = mentorPositionIndexes.get(Character.toUpperCase(novice));

                    long mentorsSeen = allMentors.subSet(leftBoundaryInclusive, rightBoundaryExclusive).size();

                    if (rightBoundaryExclusive > patternLength) {
                        // Wrap around

                        int overshoot = rightBoundaryExclusive - patternLength;
                        int numberOfFullWrapArounds = overshoot / patternLength;
                        int singleOvershoot = overshoot % patternLength;

                        mentorsSeen += (long) allMentors.size() * numberOfFullWrapArounds;

                        mentorsSeen += allMentors.subSet(0, singleOvershoot).size();
                    }

                    total += mentorsSeen;

                }

            }

            // Do a pass where the left view wraps around but the right view does not

            for (int index = 0; index < patternLength; index++) {

                char novice = chars[index];

                if (Character.isLowerCase(novice)) {

                    int leftBoundaryInclusive = index - distanceLimit;
                    int rightBoundaryExclusive = index + distanceLimit + 1;

                    SortedSet<Integer> allMentors = mentorPositionIndexes.get(Character.toUpperCase(novice));

                    long mentorsSeen = allMentors.subSet(leftBoundaryInclusive, rightBoundaryExclusive).size();

                    if (leftBoundaryInclusive < 0) {
                        // Wrap around
                        mentorsSeen += allMentors.subSet((int) (leftBoundaryInclusive + patternLength), (int) patternLength).size();

                    }

                    total += mentorsSeen;

                }

            }

            // Third pass, where both views wrap around

            for (int index = 0; index < patternLength; index++) {

                char novice = chars[index];

                if (Character.isLowerCase(novice)) {

                    int leftBoundaryInclusive = index - distanceLimit;
                    int rightBoundaryExclusive = index + distanceLimit + 1;

                    SortedSet<Integer> allMentors = mentorPositionIndexes.get(Character.toUpperCase(novice));

                    long mentorsSeen = allMentors.subSet(leftBoundaryInclusive, rightBoundaryExclusive).size();

                    if (rightBoundaryExclusive > patternLength) {
                        // Wrap around
                        mentorsSeen += allMentors.subSet(0, (int) (rightBoundaryExclusive - patternLength)).size();
                    }

                    if (leftBoundaryInclusive < 0) {
                        // Wrap around
                        mentorsSeen += allMentors.subSet((int) (leftBoundaryInclusive + patternLength), (int) patternLength).size();

                    }

                    total += mentorsSeen * (patternRepeats - 2);

                }

            }

        }

        return Long.toString(total);

    }

}
