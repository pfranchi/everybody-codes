package common.pathfinding;

public interface StateRelevancePredicate<N> {

    boolean isCurrentStateNotRelevant(SimpleNodeWithCost<N> currentState, N previousState, int previousCost);

}
