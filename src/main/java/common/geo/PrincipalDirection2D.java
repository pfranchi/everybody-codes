package common.geo;

import java.util.Collections;
import java.util.List;

public enum PrincipalDirection2D implements Direction2D {

    NORTH(ImmutableCoordinate2D.of(0, 1), ImmutableCell2D.of(-1, 0)),
    SOUTH(ImmutableCoordinate2D.of(0, -1), ImmutableCell2D.of(1, 0)),
    WEST(ImmutableCoordinate2D.of(-1, 0), ImmutableCell2D.of(0, -1)),
    EAST(ImmutableCoordinate2D.of(1, 0), ImmutableCell2D.of(0, 1)),

    NORTH_EAST(NORTH, EAST),
    NORTH_WEST(NORTH, WEST),

    SOUTH_EAST(SOUTH, EAST),
    SOUTH_WEST(SOUTH, WEST),
    ;

    private final Coordinate2D unitVectorCoordinate;

    private final Cell2D unitVectorCell;

    PrincipalDirection2D(Coordinate2D unitVectorCoordinate, Cell2D unitVectorCell) {
        this.unitVectorCoordinate = unitVectorCoordinate;
        this.unitVectorCell = unitVectorCell;
    }

    PrincipalDirection2D(PrincipalDirection2D cardinalDirection1, PrincipalDirection2D cardinalDirection2) {
        this(cardinalDirection1.getUnitVectorCoordinate().add(cardinalDirection2.getUnitVectorCoordinate()),
                cardinalDirection1.getUnitVectorCell().add(cardinalDirection2.getUnitVectorCell()));
    }

    public Coordinate2D getUnitVectorCoordinate() {
        return unitVectorCoordinate;
    }

    public Cell2D getUnitVectorCell() {
        return unitVectorCell;
    }

    public PrincipalDirection2D inverse() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
            case NORTH_EAST -> SOUTH_WEST;
            case NORTH_WEST -> SOUTH_EAST;
            case SOUTH_EAST -> NORTH_WEST;
            case SOUTH_WEST -> NORTH_EAST;
        };
    }

    public PrincipalDirection2D rotateRight() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            case NORTH_EAST -> SOUTH_EAST;
            case SOUTH_EAST -> SOUTH_WEST;
            case SOUTH_WEST -> NORTH_WEST;
            case NORTH_WEST -> NORTH_EAST;
        };
    }

    public PrincipalDirection2D rotateLeft() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
            case NORTH_EAST -> NORTH_WEST;
            case NORTH_WEST -> SOUTH_WEST;
            case SOUTH_WEST -> SOUTH_EAST;
            case SOUTH_EAST -> NORTH_EAST;
        };
    }

    public List<CardinalDirection2D> toCardinalComponents() {

        return switch (this) {
            case NORTH, SOUTH, WEST, EAST ->
                    Collections.singletonList(CardinalDirection2D.forUnitVectorCoordinate(this.getUnitVectorCoordinate()));
            case NORTH_WEST -> List.of(CardinalDirection2D.NORTH, CardinalDirection2D.WEST);
            default -> throw new UnsupportedOperationException("Method not complete yet");
        };

    }

}
