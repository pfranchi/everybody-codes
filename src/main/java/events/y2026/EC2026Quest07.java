package events.y2026;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import common.AbstractQuest;
import common.support.interfaces.MainEvent2026;
import common.support.interfaces.Quest07;
import common.support.params.ExecutionParameters;
import common.support.params.GenericExecutionParameter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("UnstableApiUsage")
public class EC2026Quest07 extends AbstractQuest implements MainEvent2026, Quest07 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ParsedInput parsedInput = ParsedInput.from(inputLines);
        Map<Integer, Long> startingEnergies = parsedInput.startingEnergies();
        Graph<Integer> graph = parsedInput.graph();
        long explosionLimit = Long.MAX_VALUE;
        int startingCloudId = parsedInput.cloudIdWithMostOutgoingConnections();
        int startingImpulseStrength = 5;

        return Long.toString(computeTotalRemainingEnergy(startingEnergies, graph, explosionLimit, startingCloudId, startingImpulseStrength));

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ParsedInput parsedInput = ParsedInput.from(inputLines);
        Map<Integer, Long> startingEnergies = parsedInput.startingEnergies();
        Graph<Integer> graph = parsedInput.graph();
        long explosionLimit = Long.MAX_VALUE;
        int startingCloudId = parsedInput.cloudIdWithMostOutgoingConnections();
        int startingImpulseStrength = 10;

        return Long.toString(computeTotalRemainingEnergy(startingEnergies, graph, explosionLimit, startingCloudId, startingImpulseStrength));

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ParsedInput parsedInput = ParsedInput.from(inputLines);
        Map<Integer, Long> startingEnergies = parsedInput.startingEnergies();
        Graph<Integer> graph = parsedInput.graph();

        long explosionLimit = 1000000;
        if (executionParameters instanceof GenericExecutionParameter(Long value)) {
            explosionLimit = value;
        }

        int startingImpulseStrength = 10;

        long maxTotalEnergy = Long.MIN_VALUE;

        for (int startingCloudId: startingEnergies.keySet()) {
            long totalEnergy = computeTotalRemainingEnergy(startingEnergies, graph, explosionLimit, startingCloudId, startingImpulseStrength);
            maxTotalEnergy = Math.max(maxTotalEnergy, totalEnergy);
        }

        return Long.toString(maxTotalEnergy);

    }


    private static final Pattern PATTERN = Pattern.compile("^id=(?<id>\\d+), energy=(?<energy>\\d+), connections=(?<connections>.*)$");

    private record ParsedInput(Map<Integer, Long> startingEnergies, ImmutableGraph<Integer> graph,
                               int cloudIdWithMostOutgoingConnections) {
        public static ParsedInput from(List<String> inputLines) {
            ImmutableGraph.Builder<Integer> graphBuilder = GraphBuilder.directed().allowsSelfLoops(true).immutable();
            Map<Integer, Long> startingEnergies = new TreeMap<>();

            int currentBestOfOutgoingEdges = -1;
            int currentBestCloudId = -1;

            for (String line : inputLines) {
                Matcher matcher = PATTERN.matcher(line);
                if (matcher.matches()) {
                    int id = Integer.parseInt(matcher.group("id"));
                    long energy = Long.parseLong(matcher.group("energy"));
                    String[] connections = matcher.group("connections").split(",");

                    startingEnergies.put(id, energy);

                    for (String connection : connections) {
                        graphBuilder.putEdge(id, Integer.parseInt(connection));
                    }

                    if (connections.length > currentBestOfOutgoingEdges) {
                        currentBestOfOutgoingEdges = connections.length;
                        currentBestCloudId = id;
                    }

                }
            }

            ImmutableGraph<Integer> graph = graphBuilder.build();

            return new ParsedInput(startingEnergies, graph, currentBestCloudId);
        }
    }

    private long computeTotalRemainingEnergy(Map<Integer, Long> startingEnergies, Graph<Integer> graph,
                                             long explosionLimit, int startingCloudId, int startingImpulseStrength) {
        Map<Integer, Long> totalEnergyByCloudId = new TreeMap<>(startingEnergies);

        Map<DestinationAndEnergy, Long> countsOfImpulsesByDestinationAndEnergy = new HashMap<>();
        countsOfImpulsesByDestinationAndEnergy.put(new DestinationAndEnergy(startingCloudId, startingImpulseStrength), 1L);

        Set<Integer> cloudIdsStillAlive = new HashSet<>(totalEnergyByCloudId.keySet());

        while (!countsOfImpulsesByDestinationAndEnergy.isEmpty()) {

            Map<DestinationAndEnergy, Long> nextImpulses = new HashMap<>();

            for (Map.Entry<DestinationAndEnergy, Long> entry: countsOfImpulsesByDestinationAndEnergy.entrySet()) {
                int destinationCloudId = entry.getKey().destinationCloudId();

                if (cloudIdsStillAlive.contains(destinationCloudId)) {

                    long impulseStrength = entry.getKey().impulseStrength();
                    long multiplicity = entry.getValue();
                    totalEnergyByCloudId.merge(destinationCloudId, impulseStrength * multiplicity, Long::sum);

                    if (impulseStrength > 1) {
                        graph.successors(destinationCloudId)
                                .stream()
                                .mapToInt(successorCloudId -> successorCloudId)
                                .filter(cloudIdsStillAlive::contains)
                                .forEach(successorCloudId -> nextImpulses.merge(new DestinationAndEnergy(successorCloudId, impulseStrength - 1), multiplicity, Long::sum));
                    }

                }

            }

            // Explosions

            for (Map.Entry<Integer, Long> totalEnergyEntry: totalEnergyByCloudId.entrySet()) {
                int explodingCloudId = totalEnergyEntry.getKey();

                if (cloudIdsStillAlive.contains(explodingCloudId)) {

                    long totalEnergy = totalEnergyEntry.getValue();

                    if (totalEnergy > explosionLimit) {
                        cloudIdsStillAlive.remove(explodingCloudId);

                        graph.successors(explodingCloudId)
                                .stream()
                                .mapToInt(successorCloudId -> successorCloudId)
                                .filter(cloudIdsStillAlive::contains)
                                .forEach(successorCloudId -> nextImpulses.merge(new DestinationAndEnergy(successorCloudId, 10L), 1L, Long::sum));

                    }

                }

            }

            countsOfImpulsesByDestinationAndEnergy = nextImpulses;

        }

        return totalEnergyByCloudId.entrySet()
                .stream()
                .filter(totalEnergyEntry -> cloudIdsStillAlive.contains(totalEnergyEntry.getKey()))
                .mapToLong(Map.Entry::getValue)
                .sum();
    }

    private record DestinationAndEnergy(int destinationCloudId, long impulseStrength) {}


}
