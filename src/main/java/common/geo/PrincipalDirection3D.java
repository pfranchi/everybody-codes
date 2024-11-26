package common.geo;

public enum PrincipalDirection3D implements Direction3D {

    // 6 directions equal to the cardinal directions
    RIGHT(ImmutableCoordinate3D.of(1, 0, 0)),
    LEFT(ImmutableCoordinate3D.of(-1, 0, 0)),

    FORWARD(ImmutableCoordinate3D.of(0, 1, 0)),
    BACKWARD(ImmutableCoordinate3D.of(0, -1, 0)),

    UP(ImmutableCoordinate3D.of(0, 0, 1)),
    DOWN(ImmutableCoordinate3D.of(0, 0, -1)),

    // 12 directions between two cardinal directions
    RIGHT_FORWARD(RIGHT, FORWARD),
    RIGHT_BACKWARD(RIGHT, BACKWARD),
    RIGHT_UP(RIGHT, UP),
    RIGHT_DOWN(RIGHT, DOWN),

    LEFT_FORWARD(LEFT, FORWARD),
    LEFT_BACKWARD(LEFT, BACKWARD),
    LEFT_UP(LEFT, UP),
    LEFT_DOWN(LEFT, DOWN),

    FORWARD_UP(FORWARD, UP),
    FORWARD_DOWN(FORWARD, DOWN),
    BACKWARD_UP(BACKWARD, UP),
    BACKWARD_DOWN(BACKWARD, DOWN),

    // 8 directions at the corners
    RIGHT_FORWARD_UP(RIGHT, FORWARD, UP),
    RIGHT_FORWARD_DOWN(RIGHT, FORWARD, DOWN),
    RIGHT_BACKWARD_UP(RIGHT, BACKWARD, UP),
    RIGHT_BACKWARD_DOWN(RIGHT, BACKWARD, DOWN),
    LEFT_FORWARD_UP(LEFT, FORWARD, UP),
    LEFT_FORWARD_DOWN(LEFT, FORWARD, DOWN),
    LEFT_BACKWARD_UP(LEFT, BACKWARD, UP),
    LEFT_BACKWARD_DOWN(LEFT, BACKWARD, DOWN);

    private final Coordinate3D unitVectorCoordinate;

    PrincipalDirection3D(Coordinate3D unitVectorCoordinate) {
        this.unitVectorCoordinate = unitVectorCoordinate;
    }

    PrincipalDirection3D(Direction3D direction1, Direction3D direction2) {
        this(direction1.getUnitVectorCoordinate().add(direction2.getUnitVectorCoordinate()));
    }

    PrincipalDirection3D(Direction3D direction1, Direction3D direction2, Direction3D direction3) {
        this(direction1.getUnitVectorCoordinate().add(direction2.getUnitVectorCoordinate()).add(direction3.getUnitVectorCoordinate()));
    }

    @Override
    public Coordinate3D getUnitVectorCoordinate() {
        return unitVectorCoordinate;
    }

    @Override
    public Direction3D inverse() {
        throw new UnsupportedOperationException();
    }
}
