package common.geo;

public interface Direction3D {

    Coordinate3D getUnitVectorCoordinate();

    Direction3D inverse();

}
