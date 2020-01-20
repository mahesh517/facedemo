package com.app.detection.Svd;

public interface AdjustableLinearSolver extends LinearSolver<DenseMatrix64F> {


    /**
     * Adds a row to A.  This has the same effect as creating a new A and calling {@link #setA}.
     *
     * @param A_row The row in A.
     * @param rowIndex Where the row appears in A.
     * @return if it succeeded or not.
     */
    public boolean addRowToA(double[] A_row, int rowIndex);

    /**
     * Removes a row from A.  This has the same effect as creating a new A and calling {@link #setA}.
     *
     * @param index which row is removed from A.
     * @return If it succeeded or not.
     */
    public boolean removeRowFromA(int index);
}
