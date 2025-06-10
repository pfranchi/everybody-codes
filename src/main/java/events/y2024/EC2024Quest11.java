package events.y2024;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import common.AbstractQuest;
import common.stats.LongMinMaxStatistics;
import common.support.interfaces.MainEvent2024;
import common.support.interfaces.Quest11;
import common.support.params.ExecutionParameters;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EC2024Quest11 extends AbstractQuest implements MainEvent2024, Quest11 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        Consumer<Map<Character, Long>> populationSetter = population -> population.put('A', 1L);
        long totalPopulation = solve(inputLines, populationSetter, 4);
        return Long.toString(totalPopulation);
    }

    private long solve(List<String> inputLines, Consumer<Map<Character, Long>> initialPopulationSetter, int numberOfDays) {

        int numberOfCategories = inputLines.size();

        NavigableSet<Character> categories = inputLines.stream().map(inputLine -> inputLine.charAt(0)).collect(Collectors.toCollection(TreeSet::new));

        Table<Character, Character, Long> matrix = HashBasedTable.create(numberOfCategories, numberOfCategories);

        for (Character category1: categories) {
            for (Character category2: categories) {
                matrix.put(category1, category2, 0L);
            }
        }

        for (String inputLine: inputLines) {
            String[] parts = inputLine.split(":");
            Character startCategory = parts[0].charAt(0);

            for (String endCategory: parts[1].split(",")) {

                Long oldValue = matrix.get(endCategory.charAt(0), startCategory);
                if (oldValue == null) {
                    throw new IllegalStateException("Cell should not be null");
                }
                long oldValueInt = oldValue;

                matrix.put(endCategory.charAt(0), startCategory, oldValueInt + 1);
            }
        }

        Map<Character, Long> population = new TreeMap<>();
        initialPopulationSetter.accept(population); // initial population

        for (int day = 1; day <= numberOfDays; day++) {
            population = growPopulation(categories, matrix, population);
        }

        return population.values().stream().mapToLong(i -> i).sum();

    }

    private <T extends Comparable<T>> Map<T, Long> growPopulation(NavigableSet<T> categories,
                                                   Table<T, T, Long> growthMatrix,
                                                   Map<T, Long> population) {

        // This is the multiplication of a matrix by a vector

        Map<T, Long> newPopulation = new TreeMap<>();
        for (T endCategory: categories) {

            Map<T, Long> rowMap = growthMatrix.row(endCategory);
            long total = categories.stream()
                    .mapToLong(startCategory -> population.getOrDefault(startCategory, 0L) * rowMap.getOrDefault(startCategory, 0L))
                    .sum();

            newPopulation.put(endCategory, total);

        }

        return newPopulation;

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Consumer<Map<Character, Long>> populationSetter = population -> population.put('Z', 1L);
        long totalPopulation = solve(inputLines, populationSetter, 10);
        return Long.toString(totalPopulation);
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfCategories = inputLines.size();

        NavigableSet<String> categories = inputLines.stream()
                .map(inputLine -> inputLine.split(":")[0])
                .collect(Collectors.toCollection(TreeSet::new));

        log("Categories are: {}", categories);

        Table<String, String, Long> matrix = HashBasedTable.create(numberOfCategories, numberOfCategories);

        for (String category1: categories) {
            for (String category2: categories) {
                matrix.put(category1, category2, 0L);
            }
        }

        for (String inputLine: inputLines) {
            String[] parts = inputLine.split(":");
            String startCategory = parts[0];

            for (String endCategory: parts[1].split(",")) {

                Long oldValue = matrix.get(endCategory, startCategory);
                if (oldValue == null) {
                    throw new IllegalStateException("Cell should not be null");
                }
                long oldValueInt = oldValue;

                matrix.put(endCategory, startCategory, oldValueInt + 1);
            }
        }

        // Solve for every possible starting category

        LongMinMaxStatistics stats = new LongMinMaxStatistics();

        for (String category: categories) {

            Map<String, Long> population = new TreeMap<>();
            population.put(category, 1L);

            for (int day = 1; day <= 20; day++) {
                population = growPopulation(categories, matrix, population);
            }

            long totalPopulation = population.values().stream().mapToLong(i -> i).sum();
            stats.accept(totalPopulation);

            log("Start category {} produces a total population of {}", category, totalPopulation);

        }

        long min = stats.getMin();
        long max = stats.getMax();

        log("Max is {}, min is {}", max, min);

        return Long.toString(max - min);
    }
}
