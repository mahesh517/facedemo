package com.app.detection.Svd;

public interface ReshapeMatrix extends Matrix {
    /**
     * Equivalent to invoking reshape(numRows,numCols,false);
     *
     * @param numRows The new number of rows in the matrix.
     * @param numCols The new number of columns in the matrix.
     */
    public void reshape(int numRows, int numCols);
}
