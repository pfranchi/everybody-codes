package common.geo;

public enum CardinalDirection2D implements Direction2D {

    NORTH(ImmutableCoordinate2D.of(0, 1), ImmutableCell2D.of(-1, 0)),
    SOUTH(ImmutableCoordinate2D.of(0, -1), ImmutableCell2D.of(1, 0)),
    WEST(ImmutableCoordinate2D.of(-1, 0), ImmutableCell2D.of(0, -1)),
    EAST(ImmutableCoordinate2D.of(1, 0), ImmutableCell2D.of(0, 1));

    private final Coordinate2D unitVectorCoordinate;

    private final Cell2D unitVectorCell;

    CardinalDirection2D(Coordinate2D unitVectorCoordinate, Cell2D unitVectorCell) {
        this.unitVectorCoordinate = unitVectorCoordinate;
        this.unitVectorCell = unitVectorCell;
    }

    public static CardinalDirection2D forUnitVectorCoordinate(Coordinate2D unitVectorCoordinate) {
        ImmutableCoordinate2D c = ImmutableCoordinate2D.copyOf(unitVectorCoordinate);
        for (CardinalDirection2D cardinalDirection2D : CardinalDirection2D.values()) {
            if (cardinalDirection2D.getUnitVectorCoordinate().equals(c)) {
                return cardinalDirection2D;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Coordinate2D getUnitVectorCoordinate() {
        return unitVectorCoordinate;
    }

    @Override
    public Cell2D getUnitVectorCell() {
        return unitVectorCell;
    }

    @Override
    public CardinalDirection2D inverse() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }

    @Override
    public CardinalDirection2D rotateRight() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }

    @Override
    public CardinalDirection2D rotateLeft() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
        };
    }

    public static CardinalDirection2D fromChar(char c) {
        return switch (c) {
            case '^', 'N' -> NORTH;
            case 'v', 'S' -> SOUTH;
            case '>', 'E' -> EAST;
            case '<', 'W' -> WEST;
            default -> throw new IllegalArgumentException();
        };
    }

    public boolean isHorizontal() {
        return this == EAST || this == WEST;
    }

    public boolean isVertical() {
        return this == NORTH || this == SOUTH;
    }


}
