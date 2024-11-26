package common.geo;

public interface Direction2D {

    Coordinate2D getUnitVectorCoordinate();

    Cell2D getUnitVectorCell();

    Direction2D inverse();

    Direction2D rotateRight();

    Direction2D rotateLeft();

}
