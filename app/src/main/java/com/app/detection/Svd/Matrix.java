package com.app.detection.Svd;

import java.io.Serializable;

/**
 * Base interface for all rectangular matrices
 *
 * @author Peter Abeles
 */
public interface Matrix extends Serializable {
    /**
     * Returns the number of rows in this matrix.
     *
     * @return Number of rows.
     */
    public int getNumRows();

    /**
     * Returns the number of columns in this matrix.
     *
     * @return Number of columns.
     */
    public int getNumCols();

    /**
     * Creates an exact copy of the matrix
     */
    public <T extends Matrix> T copy();

    /**
     * Sets this matrix to be identical to the 'original' matrix passed in.
     */
    public void set(Matrix original);

    /**
     * Prints the matrix to standard out.
     */
    public void print();
}
