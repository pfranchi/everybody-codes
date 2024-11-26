package common.geo;

public interface Coordinate3D {

    int getX();

    int getY();

    int getZ();

    Coordinate3D add(Coordinate3D other);

    default Coordinate3D add(Direction3D direction) {
        return add(direction.getUnitVectorCoordinate());
    }

    Coordinate3D minus(Coordinate3D other);

    default int taxicabDistance(Coordinate3D other) {
        return Math.abs(other.getX() - this.getX()) + Math.abs(other.getY() - this.getY()) + Math.abs(other.getZ() - this.getZ());
    }

    default int taxicabDistance() {
        return Math.abs(this.getX()) + Math.abs(this.getY()) + Math.abs(this.getZ());
    }

}
