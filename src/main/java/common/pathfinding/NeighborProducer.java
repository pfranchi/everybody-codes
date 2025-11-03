package common.pathfinding;

import java.util.Set;

public interface NeighborProducer<N> {

    Set<SimpleNodeWithCost<N>> produceNeighborsWithCosts(SimpleNodeWithCost<N> stateWithCost);

}
