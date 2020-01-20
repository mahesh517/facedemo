package com.app.detection.Svd;

public interface QRDecomposition <T extends Matrix>
        extends DecompositionInterface<T> {
    /**
     * <p>
     * Returns the Q matrix from the decomposition.  Should only
     * been called.
     * </p>
     *
     * <p>
     * If parameter Q is not null, then that matrix is used to store the Q matrix.  Otherwise
     * a new matrix is created.
     * </p>
     *
     * @param Q If not null then the Q matrix is written to it.  Modified.
     * @param compact If true an m by n matrix is created, otherwise n by n.
     * @return The Q matrix.
     */
    public T getQ(T Q, boolean compact);

    /**
     * <p>
     * Returns the R matrix from the decomposition.  Should only be
     * </p>
     * <p>
     * If setZeros is true then an n &times; m matrix is required and all the elements are set.
     * If setZeros is false then the matrix must be at least m &times; m and only the upper triangular
     * elements are set.
     * </p>
     *
     * <p>
     * If parameter R is not null, then that matrix is used to store the R matrix.  Otherwise
     * a new matrix is created.
     * </p>
     *
     * @param R If not null then the R matrix is written to it. Modified.
     * @param compact If true only the upper triangular elements are set
     * @return The R matrix.
     */
    public T getR(T R, boolean compact);
}
