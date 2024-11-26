package common.geo;

public interface Cell2D {

    int getRow();

    int getColumn();

    Cell2D add(Cell2D other);

    default Cell2D add(Direction2D other) {
        return add(other.getUnitVectorCell());
    }

    default int taxicabDistance(Cell2D other) {
        return Math.abs(other.getRow() - this.getRow()) + Math.abs(other.getColumn() - this.getColumn());
    }

    default int taxicabDistance() {
        return taxicabDistance(ImmutableCell2D.of(0, 0));
    }

}
