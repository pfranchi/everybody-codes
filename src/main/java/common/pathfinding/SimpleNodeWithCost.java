package common.pathfinding;

import java.util.Comparator;

public record SimpleNodeWithCost<N>(N node, int cost) implements NodeWithCost<N> {

    public static final Comparator<SimpleNodeWithCost<?>> CMP = Comparator.comparingInt(SimpleNodeWithCost::cost);

}
