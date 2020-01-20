package com.app.detection.Svd;

public interface CholeskyLDLDecomposition<MatrixType extends Matrix>
        extends DecompositionInterface<MatrixType> {


    /**
     * <p>
     * Returns the lower triangular matrix from the decomposition.
     * </p>
     *
     * <p>
     * If an input is provided that matrix is used to write the results to.
     * Otherwise a new matrix is created and the results written to it.
     * </p>
     *
     * @param L If not null then the decomposed matrix is written here.
     * @return A lower triangular matrix.
     */
    public MatrixType getL(MatrixType L);

    /**
     * Returns the elements in the diagonal matrix
     * @return array with diagonal elements. Array might be larger than the number of elements.
     */
    public double[] getDiagonal();

    /**
     * <p>
     * Returns the diagonal matrixfrom the decomposition.
     * </p>
     *
     * <p>
     * If an input is provided that matrix is used to write the results to.
     * Otherwise a new matrix is created and the results written to it.
     * </p>
     *
     * @param D If not null it will be used to store the diagonal matrix
     * @return D Square diagonal matrix
     */
    public MatrixType getD(MatrixType D);

}