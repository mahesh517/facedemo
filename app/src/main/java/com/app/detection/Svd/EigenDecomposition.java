package com.app.detection.Svd;

public interface EigenDecomposition<MatrixType extends Matrix>
        extends DecompositionInterface<MatrixType> {

    /**
     * Returns the number of eigenvalues/eigenvectors.  This is the matrix's dimension.
     *
     * @return number of eigenvalues/eigenvectors.
     */
    public int getNumberOfEigenvalues();

    /**
     * <p>
     * Returns an eigenvalue as a complex number.  For symmetric matrices the returned eigenvalue will always be a real
     * number, which means the imaginary component will be equal to zero.
     * </p>
     *
     * <p>
     * NOTE: The order of the eigenvalues is dependent upon the decomposition algorithm used.  This means that they may
     * or may not be ordered by magnitude.  For example the QR algorithm will returns results that are partially
     * ordered by magnitude, but this behavior should not be relied upon.
     * </p>
     *
     * @param index Index of the eigenvalue eigenvector pair.
     * @return An eigenvalue.
     */
    public Complex64F getEigenvalue(int index);

    /**
     * <p>
     * Used to retrieve real valued eigenvectors.  If an eigenvector is associated with a complex eigenvalue
     * then null is returned instead.
     * </p>
     *
     * @param index Index of the eigenvalue eigenvector pair.
     * @return If the associated eigenvalue is real then an eigenvector is returned, null otherwise.
     */
    public MatrixType getEigenVector(int index);
}
