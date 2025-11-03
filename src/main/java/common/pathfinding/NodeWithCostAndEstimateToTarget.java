package common.pathfinding;

public interface NodeWithCostAndEstimateToTarget<N> extends NodeWithCost<N> {

    int estimatedCostToTarget();

}
