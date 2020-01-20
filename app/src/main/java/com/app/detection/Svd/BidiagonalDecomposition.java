package com.app.detection.Svd;

public interface BidiagonalDecomposition <T extends Matrix>
        extends DecompositionInterface<T> {

    /**
     * Returns the bidiagonal matrix.
     *
     * @param B If not null the results are stored here, if null a new matrix is created.
     * @return The bidiagonal matrix.
     */
    public T getB(T B, boolean compact);

    /**
     * Returns the orthogonal U matrix.
     *
     * @param U If not null then the results will be stored here.  Otherwise a new matrix will be created.
     * @return The extracted Q matrix.
     */
    public T getU(T U, boolean transpose, boolean compact);


    /**
     * Returns the orthogonal V matrix.
     *
     * @param V If not null then the results will be stored here.  Otherwise a new matrix will be created.
     * @return The extracted Q matrix.
     */
    public T getV(T V, boolean transpose, boolean compact);

    /**
     * Extracts the diagonal and off diagonal elements from the decomposition.
     *
     * @param diag diagonal elements from B.
     * @param off off diagonal elements form B.
     */
    public void getDiagonal(double diag[], double off[]);

}