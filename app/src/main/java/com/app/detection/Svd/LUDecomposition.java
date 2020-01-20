package com.app.detection.Svd;

public interface LUDecomposition <T extends Matrix>
        extends DecompositionInterface<T> {

    /**
     * <p>
     * Returns the L matrix from the decomposition.  Should only
     * been called.
     * </p>
     *
     * <p>
     * If parameter 'lower' is not null, then that matrix is used to store the L matrix.  Otherwise
     * a new matrix is created.
     * </p>
     *
     * @param lower Storage for T matrix. If null then a new matrix is returned.  Modified.
     * @return The L matrix.
     */
    public T getLower(T lower);

    /**
     * <p>
     * Returns the U matrix from the decomposition.  Should only
     * been called.
     * </p>
     *
     * <p>
     * If parameter 'upper' is not null, then that matrix is used to store the U matrix.  Otherwise
     * a new matrix is created.
     * </p>
     *
     * @param upper Storage for U matrix. If null then a new matrix is returned. Modified.
     * @return The U matrix.
     */
    public T getUpper(T upper);

    /**
     * <p>
     * For numerical stability there are often row interchanges.  This computes
     * a pivot matrix that will undo those changes.
     * </p>
     *
     * @param pivot Storage for the pivot matrix. If null then a new matrix is returned. Modified.
     * @return The pivot matrix.
     */
    public T getPivot(T pivot);

    /**
     * Returns true if the decomposition detected a singular matrix.  This check
     * will not work 100% of the time due to machine precision issues.
     *
     * @return True if the matrix is singular and false if it is not.
     */
    // TODO Remove?  If singular decomposition will fail.
    public boolean isSingular();

    /**
     * Computes the matrix's determinant using the LU decomposition.
     *
     * @return The determinant.
     */
    public Complex64F computeDeterminant();
}
