package com.app.detection.Svd;

public interface TridiagonalSimilarDecomposition<MatrixType extends Matrix>
        extends DecompositionInterface<MatrixType> {

    /**
     * Extracts the tridiagonal matrix found in the decomposition.
     *
     * @param T If not null then the results will be stored here.  Otherwise a new matrix will be created.
     * @return The extracted T matrix.
     */
    public MatrixType getT(MatrixType T);

    /**
     * An orthogonal matrix that has the following property: T = Q<sup>T</sup>AQ
     *
     * @param Q If not null then the results will be stored here.  Otherwise a new matrix will be created.
     * @return The extracted Q matrix.
     */
    public MatrixType getQ(MatrixType Q, boolean transposed);

    /**
     * Extracts the diagonal and off diagonal elements of the decomposed tridiagonal matrix.
     * Since it is symmetric only one off diagonal array is returned.
     *
     * @param diag Diagonal elements. Modified.
     * @param off off diagonal elements. Modified.
     */
    public void getDiagonal(double[] diag, double[] off);
}
