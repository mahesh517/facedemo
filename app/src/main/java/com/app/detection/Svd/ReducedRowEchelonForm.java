package com.app.detection.Svd;

public interface ReducedRowEchelonForm<T extends RealMatrix64F> {

    /**
     * Puts the augmented matrix into RREF.  The coefficient matrix is stored in
     * columns less than coefficientColumns.
     *
     *
     * @param A Input: Augmented matrix.  Output: RREF.  Modified.
     * @param coefficientColumns Number of coefficients in the system matrix.
     */
    public void reduce(T A, int coefficientColumns);

    /**
     * Specifies tolerance for determining if the system is singular and it should stop processing.
     * A reasonable value is: tol = EPS/max(||tol||).
     *
     * @param tol Tolerance for singular matrix. A reasonable value is: tol = EPS/max(||tol||). Or just set to zero.
     */
    public void setTolerance(double tol);
}
