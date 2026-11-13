package events.y2026;

import com.google.common.collect.Sets;
import com.google.common.graph.*;
import common.AbstractQuest;
import common.Graphs;
import common.geo.CardinalDirection2D;
import common.geo.ImmutableCell2D;
import common.grids.Grids;
import common.support.interfaces.MainEvent2026;
import common.support.interfaces.Quest14;
import common.support.params.ExecutionParameters;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.graph.guava.ImmutableValueGraphAdapter;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class EC2026Quest14 extends AbstractQuest implements MainEvent2026, Quest14 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        int internalCount = 0;
        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex <  numberOfColumns; columnIndex++) {

                ImmutableCell2D cell = ImmutableCell2D.of(rowIndex, columnIndex);
                char c =  inputLines.get(rowIndex).charAt(columnIndex);

                for (CardinalDirection2D direction: CardinalDirection2D.values()) {
                    ImmutableCell2D neighbor = cell.add(direction);

                    if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, neighbor)) {
                        char c2 =  inputLines.get(neighbor.getRow()).charAt(neighbor.getColumn());
                        if (c != c2) {
                            internalCount++;
                        }
                    }

                }

            }
        }

        //internalCount /= 2;

        int count = internalCount / 2 + 2 * numberOfRows + 2 * numberOfColumns;

        return Integer.toString(count);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        ImmutableGraph.Builder<ImmutableCell2D> graphBuilder = GraphBuilder.undirected().immutable();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex <  numberOfColumns; columnIndex++) {

                ImmutableCell2D cell =  ImmutableCell2D.of(rowIndex, columnIndex);
                char c =  inputLines.get(rowIndex).charAt(columnIndex);

                graphBuilder.addNode(cell);

                for (CardinalDirection2D direction: CardinalDirection2D.values()) {
                    ImmutableCell2D neighbor = cell.add(direction);
                    if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, neighbor)) {
                        char c2 =  inputLines.get(neighbor.getRow()).charAt(neighbor.getColumn());
                        if (c == c2) {
                            graphBuilder.putEdge(cell, neighbor);
                        }
                    }
                }

            }
        }

        ImmutableGraph<ImmutableCell2D> graph = graphBuilder.build();

        List<List<ImmutableCell2D>> connectedComponents = Graphs.getConnectedComponents(graph);

        List<Piece> pieces = new ArrayList<>();

        for (int connectedComponentIndex = 0; connectedComponentIndex < connectedComponents.size(); connectedComponentIndex++) {
            List<ImmutableCell2D> connectedComponent =  connectedComponents.get(connectedComponentIndex);
            char c = inputLines.get(connectedComponent.getFirst().getRow()).charAt(connectedComponent.getFirst().getColumn());
            Piece piece = new Piece(connectedComponentIndex, c, connectedComponent);
            pieces.add(piece);
        }

        int numberOfPieces = pieces.size();

        //log("There are {} pieces", pieces.size());
        //pieces.forEach(this::log);

        ImmutableValueGraph.Builder<Piece, Integer> pieceGraphBuilder = ValueGraphBuilder.undirected().immutable();

        for (int piece1Index = 0; piece1Index < numberOfPieces; piece1Index++) {
            Piece piece1 = pieces.get(piece1Index);

            int edgesOnTheFrame = numberOfEdgesOnTheFrame(numberOfRows, numberOfColumns, piece1);
            if (edgesOnTheFrame > 0) {
                pieceGraphBuilder.putEdgeValue(piece1, Piece.FRAME, edgesOnTheFrame);
            }

            //log("Checking piece {} which shares {} cells with the frame", piece1,  edgesOnTheFrame);

            for (Piece piece2: pieces.subList(piece1Index + 1, numberOfPieces)) {

                int numberOfSharedEdges = numberOfSharedEdges(piece1, piece2);
                if (numberOfSharedEdges > 0) {
                    pieceGraphBuilder.putEdgeValue(piece1, piece2, numberOfSharedEdges);
                }

                //log("\tIt shares {} edges with {}", numberOfSharedEdges, piece2);

            }

            //emptyLine();

        }

        Graph<Piece, EndpointPair<Piece>> graph2 = new ImmutableValueGraphAdapter<>(pieceGraphBuilder.build(), value -> value);
        KruskalMinimumSpanningTree<Piece, EndpointPair<Piece>> kruskal = new KruskalMinimumSpanningTree<>(graph2);
        SpanningTreeAlgorithm.SpanningTree<EndpointPair<Piece>> spanningTree = kruskal.getSpanningTree();
        double treeWeight = spanningTree.getWeight();

        return Integer.toString((int) treeWeight);
    }

    private int numberOfEdgesOnTheFrame(int numberOfRows, int numberOfColumns, Piece piece) {
        int count = 0;
        for (ImmutableCell2D cell: piece.cells()) {
            for (CardinalDirection2D direction: CardinalDirection2D.values()) {
                if (!Grids.isPositionInGrid(numberOfRows, numberOfColumns, cell.add(direction))) {
                    count++;
                }
            }
        }
        return count;
    }

    private int numberOfSharedEdges(Piece piece1, Piece piece2) {

        int count = 0;
        Piece smallestPiece = piece1.cells().size() <= piece2.cells().size() ? piece1 : piece2;
        Piece otherPiece = smallestPiece == piece1 ? piece2 : piece1;
        Set<ImmutableCell2D> cellsInTheOtherPiece = new HashSet<>(otherPiece.cells());

        for (ImmutableCell2D cell: smallestPiece.cells()) {

            for (CardinalDirection2D direction: CardinalDirection2D.values()) {
                if (cellsInTheOtherPiece.contains(cell.add(direction))) {
                    count++;
                }
            }

        }

        return count;

    }

    private record Piece(int pieceIndex, char c, List<ImmutableCell2D> cells) {

        private static final Piece FRAME = new Piece(-1, '\u0000', List.of());

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfRows = inputLines.size();
        int numberOfColumns = inputLines.getFirst().length();

        ImmutableGraph.Builder<ImmutableCell2D> graphBuilder = GraphBuilder.undirected().immutable();

        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex <  numberOfColumns; columnIndex++) {

                ImmutableCell2D cell =  ImmutableCell2D.of(rowIndex, columnIndex);
                char c =  inputLines.get(rowIndex).charAt(columnIndex);

                graphBuilder.addNode(cell);

                for (CardinalDirection2D direction: CardinalDirection2D.values()) {
                    ImmutableCell2D neighbor = cell.add(direction);
                    if (Grids.isPositionInGrid(numberOfRows, numberOfColumns, neighbor)) {
                        char c2 =  inputLines.get(neighbor.getRow()).charAt(neighbor.getColumn());
                        if (c == c2) {
                            graphBuilder.putEdge(cell, neighbor);
                        }
                    }
                }

            }
        }

        ImmutableGraph<ImmutableCell2D> graph = graphBuilder.build();

        List<List<ImmutableCell2D>> connectedComponents = Graphs.getConnectedComponents(graph);

        List<Piece> pieces = new ArrayList<>();

        for (int connectedComponentIndex = 0; connectedComponentIndex < connectedComponents.size(); connectedComponentIndex++) {
            List<ImmutableCell2D> connectedComponent =  connectedComponents.get(connectedComponentIndex);
            char c = inputLines.get(connectedComponent.getFirst().getRow()).charAt(connectedComponent.getFirst().getColumn());
            Piece piece = new Piece(connectedComponentIndex, c, connectedComponent);
            pieces.add(piece);
        }

        int numberOfPieces = pieces.size();

        ImmutableValueGraph.Builder<Piece, Integer> pieceGraphBuilder = ValueGraphBuilder.undirected().immutable();

        for (int piece1Index = 0; piece1Index < numberOfPieces; piece1Index++) {
            Piece piece1 = pieces.get(piece1Index);

            int edgesOnTheFrame = numberOfEdgesOnTheFrame(numberOfRows, numberOfColumns, piece1);
            if (edgesOnTheFrame > 0) {
                pieceGraphBuilder.putEdgeValue(piece1, Piece.FRAME, edgesOnTheFrame);
            }

            //log("Checking piece {} which shares {} cells with the frame", piece1,  edgesOnTheFrame);

            for (Piece piece2: pieces.subList(piece1Index + 1, numberOfPieces)) {

                int numberOfSharedEdges = numberOfSharedEdges(piece1, piece2);
                if (numberOfSharedEdges > 0) {
                    pieceGraphBuilder.putEdgeValue(piece1, piece2, numberOfSharedEdges);
                }

                //log("\tIt shares {} edges with {}", numberOfSharedEdges, piece2);

            }

            //emptyLine();

        }

        ImmutableValueGraph<Piece, Integer> pieceGraph = pieceGraphBuilder.build();

        double minWeight = Double.MAX_VALUE;

        for (int piece1Index = 0; piece1Index < numberOfPieces; piece1Index++) {
            Piece piece1 = pieces.get(piece1Index);
            Set<Piece> piecesAdjacentToPiece1 = new HashSet<>(pieceGraph.adjacentNodes(piece1));
            boolean piece1AdjacentToFrame = piecesAdjacentToPiece1.contains(Piece.FRAME);
            piecesAdjacentToPiece1.remove(Piece.FRAME);

            char c1 = piece1.c();

            if (c1 == 'P' || c1 == 'S' || c1 == 'A' || c1 == 'G') {
                boolean breakpoint = true;
            }

            Piece superPiece1 = new Piece(-2, '\u0000', List.of());

            for (Piece piece2: pieces.subList(piece1Index + 1, numberOfPieces)) {

                Set<Piece> piecesAdjacentToPiece2 = new HashSet<>(pieceGraph.adjacentNodes(piece2));
                boolean piece2AdjacentToFrame = piecesAdjacentToPiece2.contains(Piece.FRAME);
                piecesAdjacentToPiece2.remove(Piece.FRAME);

                char c2 = piece2.c();
                if (c2 == 'P' || c2 == 'S' || c2 == 'A' || c2 == 'G') {
                    boolean breakpoint = true;
                }

                Piece superPiece2 = new Piece(-3, '\u0000', List.of());

                boolean piece1AndPiece2AreConnectedByAtMostOnePiece = piecesAdjacentToPiece1.contains(piece2) ||
                                                                      !Sets.intersection(piecesAdjacentToPiece1, piecesAdjacentToPiece2).isEmpty();

                MutableValueGraph<Piece, Integer> copy = com.google.common.graph.Graphs.copyOf(pieceGraph);

                // Replace them with one or two super-nodes

                if (piece1AndPiece2AreConnectedByAtMostOnePiece) {

                    Set<Piece> relevantPieces = new HashSet<>(piecesAdjacentToPiece1);
                    relevantPieces.addAll(piecesAdjacentToPiece2);
                    relevantPieces.add(piece1);
                    relevantPieces.add(piece2);

                    relevantPieces.forEach(copy::removeNode);

                    addSuperPieceAndConnections(pieces, pieceGraph, copy, relevantPieces, superPiece1);

                    if (!piece1AdjacentToFrame && !piece2AdjacentToFrame) {

                        relevantPieces.stream()
                                .map(pieceAbsorbedByTheSuperNode -> pieceGraph.edgeValue(pieceAbsorbedByTheSuperNode, Piece.FRAME))
                                .filter(Optional::isPresent)
                                .mapToInt(Optional::get)
                                .min()
                                .ifPresent(minWeightForFrame -> copy.putEdgeValue(superPiece1, Piece.FRAME,  minWeightForFrame));

                    }

                } else {

                    Set<Piece> relevantPieces1 = new HashSet<>(piecesAdjacentToPiece1);
                    relevantPieces1.add(piece1);

                    relevantPieces1.forEach(copy::removeNode);
                    addSuperPieceAndConnections(pieces, pieceGraph, copy, relevantPieces1, superPiece1);

                    if (!piece1AdjacentToFrame) {
                        relevantPieces1.stream()
                                .map(p -> pieceGraph.edgeValue(p, Piece.FRAME))
                                .filter(Optional::isPresent).mapToInt(Optional::get)
                                .min()
                                .ifPresent(w -> copy.putEdgeValue(superPiece1, Piece.FRAME, w));
                    }

                    Set<Piece> relevantPieces2 = new HashSet<>(piecesAdjacentToPiece2);
                    relevantPieces2.add(piece2);

                    relevantPieces2.forEach(copy::removeNode);
                    addSuperPieceAndConnections(pieces, pieceGraph, copy, relevantPieces2, superPiece2);

                    if (!piece2AdjacentToFrame) {
                        relevantPieces2.stream()
                                .map(p -> pieceGraph.edgeValue(p, Piece.FRAME))
                                .filter(Optional::isPresent).mapToInt(Optional::get)
                                .min()
                                .ifPresent(w -> copy.putEdgeValue(superPiece1, Piece.FRAME, w));
                    }

                    // Possibly add a connection between superPiece1 and superPiece2

                    int minWeightBetweenSuperPieces = Integer.MAX_VALUE;
                    boolean anyConnectionBetweenSuperPieces = false;
                    for (Piece pieceAbsorbedIntoSuperPiece1: relevantPieces1) {
                        for (Piece pieceAbsorbedIntoSuperPiece2: relevantPieces2) {

                            Optional<Integer> o = pieceGraph.edgeValue(pieceAbsorbedIntoSuperPiece1, pieceAbsorbedIntoSuperPiece2);

                            if (o.isPresent()) {
                                anyConnectionBetweenSuperPieces = true;
                                minWeightBetweenSuperPieces = Math.min(minWeightBetweenSuperPieces, o.get());
                            }

                        }
                    }

                    if (anyConnectionBetweenSuperPieces) {
                        copy.putEdgeValue(superPiece1, superPiece2, minWeightBetweenSuperPieces);
                    }

                }

                ImmutableValueGraph<Piece, Integer> immutableCopy = ImmutableValueGraph.copyOf(copy);

                Graph<Piece, EndpointPair<Piece>> graph2 = new ImmutableValueGraphAdapter<>(immutableCopy, n -> n);
                KruskalMinimumSpanningTree<Piece, EndpointPair<Piece>> kruskal = new KruskalMinimumSpanningTree<>(graph2);
                SpanningTreeAlgorithm.SpanningTree<EndpointPair<Piece>> spanningTree = kruskal.getSpanningTree();
                double treeWeight = spanningTree.getWeight();

                //log("When removing pieces {} and {} the weight is {}", piece1, piece2, treeWeight);

                minWeight = Math.min(minWeight, treeWeight);

            }

            log("Completed analysis for piece 1: {}", piece1);
            log("Min so far = {}", minWeight);

        }

        log("Min weight = {}", minWeight);

        return Integer.toString((int) minWeight);

    }

    private void addSuperPieceAndConnections(List<Piece> pieces, ValueGraph<Piece, Integer> pieceGraph,
                                             MutableValueGraph<Piece, Integer> copy, Set<Piece> relevantPieces, Piece superPiece) {
        // Replace the old nodes with a single super-node
        copy.addNode(superPiece);

        // Add connections from the super-node to each piece that was not replaced by the
        // super-node and that was connected to a piece now replaced by the super-node
        // The weight is the minimum weight of the external piece across all nodes that
        // were connected to it and that are now part of the super-node.

        for (Piece externalPiece: pieces) {
            if (!relevantPieces.contains(externalPiece)) {

                // Check if this external piece had a connection to a piece that is now part of the super-node

                int minWeightForThisExternalPiece = Integer.MAX_VALUE;
                boolean connectsToTheSuperNode = false;
                for (Piece pieceAbsorbedByTheSuperNode: relevantPieces) {

                    Optional<Integer> o = pieceGraph.edgeValue(pieceAbsorbedByTheSuperNode, externalPiece);

                    if (o.isPresent()) {

                        connectsToTheSuperNode = true;
                        int weight = o.get();
                        minWeightForThisExternalPiece = Math.min(minWeightForThisExternalPiece, weight);

                    }

                }

                // If so, add a new node to the copy between the super-node and the external node
                if (connectsToTheSuperNode) {
                    copy.putEdgeValue(superPiece, externalPiece, minWeightForThisExternalPiece);
                }

            }
        }
    }

}
