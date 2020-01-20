package com.app.detection.Svd;

public class LinearSolverQr_D64 extends LinearSolverAbstract_D64 {

    private QRDecomposition<DenseMatrix64F> decomposer;

    protected int maxRows = -1;
    protected int maxCols = -1;

    protected DenseMatrix64F Q;
    protected DenseMatrix64F R;

    private DenseMatrix64F Y,Z;

    /**
     * Creates a linear solver that uses QR decomposition.
     *
     */
    public LinearSolverQr_D64(QRDecomposition<DenseMatrix64F> decomposer) {
        this.decomposer = decomposer;
    }

    /**
     * Changes the size of the matrix it can solve for
     *
     * @param maxRows Maximum number of rows in the matrix it will decompose.
     * @param maxCols Maximum number of columns in the matrix it will decompose.
     */
    public void setMaxSize( int maxRows , int maxCols )
    {
        this.maxRows = maxRows; this.maxCols = maxCols;

        Q = new DenseMatrix64F(maxRows,maxRows);
        R = new DenseMatrix64F(maxRows,maxCols);

        Y = new DenseMatrix64F(maxRows,1);
        Z = new DenseMatrix64F(maxRows,1);
    }

    /**
     * Performs QR decomposition on A
     *
     * @param A not modified.
     */
    @Override
    public boolean setA(DenseMatrix64F A) {
        if( A.numRows > maxRows || A.numCols > maxCols ) {
            setMaxSize(A.numRows,A.numCols);
        }

        _setA(A);
        if( !decomposer.decompose(A) )
            return false;

        Q.reshape(numRows,numRows, false);
        R.reshape(numRows,numCols, false);
        decomposer.getQ(Q,false);
        decomposer.getR(R,false);

        return true;
    }

    @Override
    public double quality() {
        return SpecializedOps.qualityTriangular(R);
    }

    /**
     * Solves for X using the QR decomposition.
     *
     * @param B A matrix that is n by m.  Not modified.
     * @param X An n by m matrix where the solution is written to.  Modified.
     */
    @Override
    public void solve(DenseMatrix64F B, DenseMatrix64F X) {
        if( X.numRows != numCols )
            throw new IllegalArgumentException("Unexpected dimensions for X");
        else if( B.numRows != numRows || B.numCols != X.numCols )
            throw new IllegalArgumentException("Unexpected dimensions for B");

        int BnumCols = B.numCols;

        Y.reshape(numRows,1, false);
        Z.reshape(numRows,1, false);

        // solve each column one by one
        for( int colB = 0; colB < BnumCols; colB++ ) {

            // make a copy of this column in the vector
            for( int i = 0; i < numRows; i++ ) {
                Y.data[i] = B.get(i,colB);
            }

            // Solve Qa=b
            // a = Q'b
            CommonOps.multTransA(Q,Y,Z);

            // solve for Rx = b using the standard upper triangular solver
            TriangularSolver.solveU(R.data,Z.data,numCols);

            // save the results
            for( int i = 0; i < numCols; i++ ) {
                X.set(i,colB,Z.data[i]);
            }
        }
    }

    @Override
    public boolean modifiesA() {
        return decomposer.inputModified();
    }

    @Override
    public boolean modifiesB() {
        return false;
    }

    @Override
    public QRDecomposition<DenseMatrix64F> getDecomposition() {
        return decomposer;
    }

    public QRDecomposition<DenseMatrix64F> getDecomposer() {
        return decomposer;
    }

    public DenseMatrix64F getQ() {
        return Q;
    }

    public DenseMatrix64F getR() {
        return R;
    }
}