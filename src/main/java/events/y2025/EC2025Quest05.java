package events.y2025;

import common.AbstractQuest;
import common.stats.LongMinMaxStatistics;
import common.stats.Statistics;
import common.support.interfaces.MainEvent2025;
import common.support.interfaces.Quest05;
import common.support.params.ExecutionParameters;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EC2025Quest05 extends AbstractQuest implements MainEvent2025, Quest05 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return Long.toString(createSword(inputLines.getFirst()).quality);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        LongMinMaxStatistics statistics = inputLines.stream().map(this::createSword).collect(Statistics.computingLongStats(sword -> sword.quality));
        return Long.toString(statistics.getMax() - statistics.getMin());

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<Sword> swords = inputLines.stream()
                .map(this::createSword)
                .sorted(Comparator.reverseOrder())
                .toList();

        int checksum = IntStream.range(0, swords.size()).map(swordIndex -> (swordIndex + 1) * swords.get(swordIndex).id).sum();

        return Integer.toString(checksum);
    }

    private static class SpineSegment {
        private boolean leftPopulated;
        private int left;
        private int center;
        private boolean rightPopulated;
        private int right;

        @Override
        @Nonnull
        public String toString() {
            return (leftPopulated ? left + "-" : "  ") + center + (rightPopulated ? "-" + right : "");
        }

    }

    private static class Sword implements Comparable<Sword> {
        private int id;
        private long quality;
        private List<Long> spineSegmentValues;

        @Override
        public int compareTo(Sword otherSword) {

            int cmp1 = Long.compare(this.quality, otherSword.quality);

            if (cmp1 != 0) {
                return cmp1;
            }

            int minSize = Integer.min(this.spineSegmentValues.size(), otherSword.spineSegmentValues.size());

            for (int levelIndex = 0; levelIndex < minSize; levelIndex++) {

                long spineSegmentValue1 = this.spineSegmentValues.get(levelIndex);
                long spineSegmentValue2 = otherSword.spineSegmentValues.get(levelIndex);

                int cmp2 = Long.compare(spineSegmentValue1, spineSegmentValue2);

                if (cmp2 != 0) {
                    return cmp2;
                }

            }

            return Integer.compare(this.id, otherSword.id);

        }
    }


    private Sword createSword(String inputLine) {

        String[] parts = inputLine.split(":");
        int id = Integer.parseInt(parts[0]);
        String inputNumbers = parts[1];


        List<Integer> numbers = Arrays.stream(inputNumbers.split(","))
                .map(Integer::parseInt).toList();

        List<SpineSegment> spineSegments = new ArrayList<>();

        for (int number: numbers) {

            boolean addedToAnExistingSpineSegment = false;

            for (SpineSegment spineSegment : spineSegments) {

                int center = spineSegment.center;

                if (!spineSegment.leftPopulated && number < center) {
                    spineSegment.leftPopulated = true;
                    spineSegment.left = number;
                    addedToAnExistingSpineSegment = true;
                    break;
                } else if (!spineSegment.rightPopulated && number > center) {
                    spineSegment.rightPopulated = true;
                    spineSegment.right = number;
                    addedToAnExistingSpineSegment = true;
                    break;
                }

            }

            if (!addedToAnExistingSpineSegment) {
                SpineSegment spineSegment = new SpineSegment();
                spineSegment.center = number;
                spineSegments.add(spineSegment);
            }

        }

        long quality = Long.parseLong(spineSegments.stream().mapToInt(segment -> segment.center)
                .mapToObj(Integer::toString).collect(Collectors.joining()));

        List<Long> spineSegmentValues = new ArrayList<>();
        for (SpineSegment spineSegment: spineSegments) {

            String s = "";

            if (spineSegment.leftPopulated) {
                s += spineSegment.left;
            }

            s += spineSegment.center;

            if (spineSegment.rightPopulated) {
                s += spineSegment.right;
            }

            spineSegmentValues.add(Long.parseLong(s));

        }

        Sword sword = new Sword();
        sword.id = id;
        sword.quality = quality;
        sword.spineSegmentValues = spineSegmentValues;

        return sword;

    }

}
