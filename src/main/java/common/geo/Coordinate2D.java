package common.geo;

public interface Coordinate2D {

    int getX();

    int getY();

    Coordinate2D add(Coordinate2D other);

    default Coordinate2D add(Direction2D other) {
        return add(other.getUnitVectorCoordinate());
    }

    default int taxicabDistance(Coordinate2D other) {
        return Math.abs(other.getX() - this.getX()) + Math.abs(other.getY() - this.getY());
    }

    default int taxicabDistance() {
        return taxicabDistance(ImmutableCoordinate2D.atOrigin());
    }

}
