package com.app.detection.Svd;

public class SwitchingEigenDecomposition
        implements EigenDecomposition<DenseMatrix64F> {
    // tolerance used in deciding if a matrix is symmetric or not
    private double tol;

    EigenDecomposition<DenseMatrix64F> symmetricAlg;
    EigenDecomposition<DenseMatrix64F> generalAlg;

    boolean symmetric;
    // should it compute eigenvectors or just eigenvalues?
    boolean computeVectors;

    DenseMatrix64F A = new DenseMatrix64F(1,1);

    /**
     *
     * @param computeVectors
     * @param tol Tolerance for a matrix being symmetric
     */
    public SwitchingEigenDecomposition( int matrixSize , boolean computeVectors , double tol ) {
        symmetricAlg = DecompositionFactory.eig(matrixSize,computeVectors,true);
        generalAlg = DecompositionFactory.eig(matrixSize,computeVectors,false);
        this.computeVectors = computeVectors;
        this.tol = tol;
    }

    public SwitchingEigenDecomposition( int matrixSize ) {
        this(matrixSize,true,1e-8);
    }

    @Override
    public int getNumberOfEigenvalues() {
        return symmetric ? symmetricAlg.getNumberOfEigenvalues() :
                generalAlg.getNumberOfEigenvalues();
    }

    @Override
    public Complex64F getEigenvalue(int index) {
        return symmetric ? symmetricAlg.getEigenvalue(index) :
                generalAlg.getEigenvalue(index);
    }

    @Override
    public DenseMatrix64F getEigenVector(int index) {
        if( !computeVectors )
            throw new IllegalArgumentException("Configured to not compute eignevectors");

        return symmetric ? symmetricAlg.getEigenVector(index) :
                generalAlg.getEigenVector(index);
    }

    @Override
    public boolean decompose(DenseMatrix64F orig) {
        A.set(orig);

        symmetric = MatrixFeatures.isSymmetric(A,tol);

        return symmetric ?
                symmetricAlg.decompose(A) :
                generalAlg.decompose(A);

    }

    @Override
    public boolean inputModified() {
        // since it doesn't know which algorithm will be used until a matrix is provided make a copy
        // of all inputs
        return false;
    }
}
