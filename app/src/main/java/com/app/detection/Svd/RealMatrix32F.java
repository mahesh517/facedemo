package com.app.detection.Svd;

public interface RealMatrix32F extends Matrix {

    /**
     * Returns the value of value of the specified matrix element.
     *
     * @param row Matrix element's row index..
     * @param col Matrix element's column index.
     * @return The specified element's value.
     */
    public float get(int row, int col);

    /**
     * Same as {@link #get} but does not perform bounds check on input parameters.  This results in about a 25%
     * speed increase but potentially sacrifices stability and makes it more difficult to track down simple errors.
     * It is not recommended that this function be used, except in highly optimized code where the bounds are
     * implicitly being checked.
     *
     * @param row Matrix element's row index..
     * @param col Matrix element's column index.
     * @return The specified element's value.
     */
    public float unsafe_get(int row, int col);

    /**
     * Sets the value of the specified matrix element.
     *
     * @param row Matrix element's row index..
     * @param col Matrix element's column index.
     * @param val The element's new value.
     */
    public void set(int row, int col, float val);

    /**
     * Same as {@link #set} but does not perform bounds check on input parameters.  This results in about a 25%
     * speed increase but potentially sacrifices stability and makes it more difficult to track down simple errors.
     * It is not recommended that this function be used, except in highly optimized code where the bounds are
     * implicitly being checked.
     *
     * @param row Matrix element's row index..
     * @param col Matrix element's column index.
     * @param val The element's new value.
     */
    public void unsafe_set(int row, int col, float val);

    /**
     * Returns the number of elements in this matrix, which is the number of rows
     * times the number of columns.
     *
     * @return Number of elements in this matrix.
     */
    public int getNumElements();
}

