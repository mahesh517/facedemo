package com.app.detection.Svd;

public abstract interface SingularValueDecomposition <T extends Matrix>
        extends DecompositionInterface<T> {

    /**
     * Returns the singular values.  This is the diagonal elements of the W matrix in the decomposition.
     * <b>Ordering of singular values is not guaranteed.</b>.
     *
     * @return Singular values. Note this array can be longer than the number of singular values.
     * Extra elements have no meaning.
     */
    public double [] getSingularValues();

    /**
     * The number of singular values in the matrix. This is equal to the length of the smallest side.
     *
     * @return Number of singular values in the matrix.
     */
    public int numberOfSingularValues();

    /**
     * If true then compact matrices are returned.
     *
     * @return true if results use compact notation.
     */
    public boolean isCompact();

    /**
     * <p>
     * Returns the orthogonal 'U' matrix.
     * </p>
     * <p>
     * Internally the SVD algorithm might compute U transposed or it might not.  To avoid an
     * unnecessary double transpose the option is provided to select if the transpose is returned.
     * </p>
     *
     * @param U Optional storage for U. If null a new instance or internally maintained matrix is returned.  Modified.
     * @param transposed If the returned U is transposed.
     * @return An orthogonal matrix.
     */
    public T getU(T U, boolean transposed);

    /**
     * <p>
     * Returns the orthogonal 'V' matrix.
     * </p>
     *
     * <p>
     * Internally the SVD algorithm might compute V transposed or it might not.  To avoid an
     * unnecessary double transpose the option is provided to select if the transpose is returned.
     * </p>
     *
     * @param V Optional storage for v. If null a new instance or internally maintained matrix is returned.  Modified.
     * @param transposed If the returned V is transposed.
     * @return An orthogonal matrix.
     */
    public T getV(T V, boolean transposed);

    /**
     * Returns a diagonal matrix with the singular values.  Order of the singular values
     * is not guaranteed.
     *
     * @param W Optional storage for W. If null a new instance or internally maintained matrix is returned.  Modified.
     * @return Diagonal matrix with singular values along the diagonal.
     */
    public T getW(T W);

    /**
     * Number of rows in the decomposed matrix.
     * @return Number of rows in the decomposed matrix.
     */
    public int numRows();

    /**
     * Number of columns in the decomposed matrix.
     * @return Number of columns in the decomposed matrix.
     */
    public int numCols();
}
