package common.geo;

public interface Coordinate4D {

    int getX();

    int getY();

    int getZ();

    int getW();

    Coordinate4D add(Coordinate4D other);

    Coordinate4D minus(Coordinate4D other);

    default int taxicabDistance(Coordinate4D other) {
        return Math.abs(other.getX() - this.getX()) + Math.abs(other.getY() - this.getY()) +
                Math.abs(other.getZ() - this.getZ()) + Math.abs(other.getW() - this.getW());
    }

    default int taxicabDistance() {
        return Math.abs(this.getX()) + Math.abs(this.getY()) +
                Math.abs(this.getZ()) + Math.abs(this.getW());
    }

}
