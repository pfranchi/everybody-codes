package common.geo;

public enum CardinalDirection3D implements Direction3D {

    RIGHT(ImmutableCoordinate3D.of(1, 0, 0)),
    LEFT(ImmutableCoordinate3D.of(-1, 0, 0)),

    FORWARD(ImmutableCoordinate3D.of(0, 1, 0)),
    BACKWARD(ImmutableCoordinate3D.of(0, -1, 0)),

    UP(ImmutableCoordinate3D.of(0, 0, 1)),
    DOWN(ImmutableCoordinate3D.of(0, 0, -1));

    private final Coordinate3D unitVectorCoordinate;

    CardinalDirection3D(Coordinate3D unitVectorCoordinate) {
        this.unitVectorCoordinate = unitVectorCoordinate;
    }


    @Override
    public Coordinate3D getUnitVectorCoordinate() {
        return unitVectorCoordinate;
    }

    @Override
    public Direction3D inverse() {
        return switch (this) {
            case RIGHT -> LEFT;
            case LEFT -> RIGHT;
            case FORWARD -> BACKWARD;
            case BACKWARD -> FORWARD;
            case UP -> DOWN;
            case DOWN -> UP;
        };
    }
}
