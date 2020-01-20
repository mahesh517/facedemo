package com.app.detection.Svd;

public class LinearSolverQrHouseCol_D64 extends LinearSolverAbstract_D64 {

    private QRDecompositionHouseholderColumn_D64 decomposer;

    private DenseMatrix64F a = new DenseMatrix64F(1,1);
    private DenseMatrix64F temp = new DenseMatrix64F(1,1);

    protected int maxRows = -1;
    protected int maxCols = -1;

    private double[][] QR; // a column major QR matrix
    private DenseMatrix64F R = new DenseMatrix64F(1,1);
    private double gammas[];

    /**
     * Creates a linear solver that uses QR decomposition.
     */
    public LinearSolverQrHouseCol_D64() {
        decomposer = new QRDecompositionHouseholderColumn_D64();
    }

    public void setMaxSize( int maxRows , int maxCols )
    {
        this.maxRows = maxRows; this.maxCols = maxCols;
    }

    /**
     * Performs QR decomposition on A
     *
     * @param A not modified.
     */
    @Override
    public boolean setA(DenseMatrix64F A) {
        if( A.numRows < A.numCols )
            throw new IllegalArgumentException("Can't solve for wide systems.  More variables than equations.");
        if( A.numRows > maxRows || A.numCols > maxCols )
            setMaxSize(A.numRows,A.numCols);

        R.reshape(A.numCols,A.numCols);
        a.reshape(A.numRows,1);
        temp.reshape(A.numRows,1);

        _setA(A);
        if( !decomposer.decompose(A) )
            return false;

        gammas = decomposer.getGammas();
        QR = decomposer.getQR();
        decomposer.getR(R,true);
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
            throw new IllegalArgumentException("Unexpected dimensions for X: X rows = "+X.numRows+" expected = "+numCols);
        else if( B.numRows != numRows || B.numCols != X.numCols )
            throw new IllegalArgumentException("Unexpected dimensions for B");

        int BnumCols = B.numCols;

        // solve each column one by one
        for( int colB = 0; colB < BnumCols; colB++ ) {

            // make a copy of this column in the vector
            for( int i = 0; i < numRows; i++ ) {
                a.data[i] = B.data[i*BnumCols + colB];
            }

            // Solve Qa=b
            // a = Q'b
            // a = Q_{n-1}...Q_2*Q_1*b
            //
            // Q_n*b = (I-gamma*u*u^T)*b = b - u*(gamma*U^T*b)
            for( int n = 0; n < numCols; n++ ) {
                double []u = QR[n];

                double vv = u[n];
                u[n] = 1;
                QrHelperFunctions_D64.rank1UpdateMultR(a, u, gammas[n], 0, n, numRows, temp.data);
                u[n] = vv;
            }

            // solve for Rx = b using the standard upper triangular solver
            TriangularSolver.solveU(R.data,a.data,numCols);

            // save the results
            for( int i = 0; i < numCols; i++ ) {
                X.data[i*X.numCols+colB] = a.data[i];
            }
        }
    }

    @Override
    public boolean modifiesA() {
        return false;
    }

    @Override
    public boolean modifiesB() {
        return false;
    }

    @Override
    public QRDecomposition<DenseMatrix64F> getDecomposition() {
        return decomposer;
    }
}