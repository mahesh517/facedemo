package com.app.detection.Svd;

public interface QRPDecomposition <T extends Matrix>
        extends QRDecomposition<T>
{
    /**
     * <p>
     * Specifies the threshold used to flag a column as being singular.  The specified threshold is relative
     * and will very depending on the system.  The default value is UtilEJML.EPS.
     * </p>
     *
     * @param threshold Singular threshold.
     */
    public void setSingularThreshold(double threshold);

    /**
     * Returns the rank as determined by the algorithm.  This is dependent upon a fixed threshold
     * and might not be appropriate for some applications.
     *
     * @return Matrix's rank
     */
    public int getRank();

    /**
     * Ordering of each column after pivoting.   The current column i was original at column pivot[i].
     *
     * @return Order of columns.
     */
    public int[] getPivots();

    /**
     * Creates the pivot matrix.
     *
     * @param P Optional storage for pivot matrix.  If null a new matrix will be created.
     * @return The pivot matrix.
     */
    public DenseMatrix64F getPivotMatrix(DenseMatrix64F P);
}
