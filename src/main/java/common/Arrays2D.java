package common;

public final class Arrays2D {

    private Arrays2D() {
    }

    public static char[][] transpose(char[][] arr) {

        int rows = arr.length;
        int columns = arr[0].length;

        char[][] transposed = new char[columns][rows];

        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            char[] row = arr[rowIndex];

            for (int columnIndex = 0; columnIndex < columns; columnIndex++) {
                transposed[columnIndex][rowIndex] = row[columnIndex];
            }
        }

        return transposed;

    }

}
