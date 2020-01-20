package com.app.detection.Svd;

public abstract class BaseLinearSolverQrp_D64 extends LinearSolverAbstract_D64 {

    QRPDecomposition<DenseMatrix64F> decomposition;

    // if true then only the basic solution will be found
    protected boolean norm2Solution;

    protected DenseMatrix64F Y = new DenseMatrix64F(1,1);
    protected DenseMatrix64F R = new DenseMatrix64F(1,1);

    // stores sub-matrices inside the R matrix
    protected DenseMatrix64F R11 = new DenseMatrix64F(1,1);

    // store an identity matrix for computing the inverse
    protected DenseMatrix64F I = new DenseMatrix64F(1,1);

    // rank of the system matrix
    protected int rank;

    protected LinearSolver<DenseMatrix64F> internalSolver = LinearSolverFactory.leastSquares(1, 1);

    // used to compute optimal 2-norm solution
    private DenseMatrix64F W = new DenseMatrix64F(1,1);

    /**
     * Configures internal parameters.
     *
     * @param decomposition Used to solve the linear system.
     * @param norm2Solution If true then the optimal 2-norm solution will be computed for degenerate systems.
     */
    protected BaseLinearSolverQrp_D64(QRPDecomposition<DenseMatrix64F> decomposition,
                                      boolean norm2Solution)
    {
        this.decomposition = decomposition;
        this.norm2Solution = norm2Solution;

        if( internalSolver.modifiesA() )
            internalSolver = new LinearSolverSafe<DenseMatrix64F>(internalSolver);
    }

    @Override
    public boolean setA(DenseMatrix64F A) {
        _setA(A);

        if( !decomposition.decompose(A) )
            return false;

        rank = decomposition.getRank();

        R.reshape(numRows,numCols);
        decomposition.getR(R,false);

        // extract the r11 triangle sub matrix
        R11.reshape(rank, rank);
        CommonOps.extract(R, 0, rank, 0, rank, R11, 0, 0);

        if( norm2Solution && rank < numCols ) {
            // extract the R12 sub-matrix
            W.reshape(rank,numCols - rank);
            CommonOps.extract(R,0,rank,rank,numCols,W,0,0);

            // W=inv(R11)*R12
            TriangularSolver.solveU(R11.data, 0, R11.numCols, R11.numCols, W.data, 0, W.numCols, W.numCols);

            // set the identity matrix in the upper portion
            W.reshape(numCols, W.numCols,true);

            for( int i = 0; i < numCols-rank; i++ ) {
                for( int j = 0; j < numCols-rank; j++ ) {
                    if( i == j )
                        W.set(i+rank,j,-1);
                    else
                        W.set(i+rank,j,0);
                }
            }
        }

        return true;
    }

    @Override
    public double quality() {
        return SpecializedOps.qualityTriangular(R);
    }

    /**
     * <p>
     * Upgrades the basic solution to the optimal 2-norm solution.
     * </p>
     *
     * <pre>
     * First solves for 'z'
     *
     *       || x_b - P*[ R_11^-1 * R_12 ] * z ||2
     * min z ||         [ - I_{n-r}      ]     ||
     *
     * </pre>
     *
     * @param X basic solution, also output solution
     */
    protected void upgradeSolution( DenseMatrix64F X ) {
        DenseMatrix64F z = Y; // recycle Y

        // compute the z which will minimize the 2-norm of X
        // because of the identity matrix tacked onto the end 'A' should never be singular
        if( !internalSolver.setA(W) )
            throw new RuntimeException("This should never happen.  Is input NaN?");
        z.reshape(numCols-rank,1);
        internalSolver.solve(X, z);

        // compute X by tweaking the original
        CommonOps.multAdd(-1, W, z, X);
    }

    @Override
    public void invert(DenseMatrix64F A_inv) {
        if( A_inv.numCols != numRows || A_inv.numRows != numCols )
            throw new IllegalArgumentException("Unexpected dimensions for A_inv");

        I.reshape(numRows, numRows);
        CommonOps.setIdentity(I);

        solve(I, A_inv);
    }

    public QRPDecomposition<DenseMatrix64F> getDecomposition() {
        return decomposition;
    }
}
