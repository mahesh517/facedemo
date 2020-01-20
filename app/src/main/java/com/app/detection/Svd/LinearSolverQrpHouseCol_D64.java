package com.app.detection.Svd;

public class LinearSolverQrpHouseCol_D64 extends BaseLinearSolverQrp_D64 {

    // Computes the QR decomposition
    private QRColPivDecompositionHouseholderColumn_D64 decomposition;

    // storage for basic solution
    private DenseMatrix64F x_basic = new DenseMatrix64F(1,1);

    public LinearSolverQrpHouseCol_D64(QRColPivDecompositionHouseholderColumn_D64 decomposition,
                                       boolean norm2Solution)
    {
        super(decomposition,norm2Solution);
        this.decomposition = decomposition;
    }

    @Override
    public void solve(DenseMatrix64F B, DenseMatrix64F X) {
        if( X.numRows != numCols )
            throw new IllegalArgumentException("Unexpected dimensions for X");
        else if( B.numRows != numRows || B.numCols != X.numCols )
            throw new IllegalArgumentException("Unexpected dimensions for B");

        int BnumCols = B.numCols;

        // get the pivots and transpose them
        int pivots[] = decomposition.getPivots();

        double qr[][] = decomposition.getQR();
        double gammas[] = decomposition.getGammas();

        // solve each column one by one
        for( int colB = 0; colB < BnumCols; colB++ ) {
            x_basic.reshape(numRows, 1);
            Y.reshape(numRows,1);

            // make a copy of this column in the vector
            for( int i = 0; i < numRows; i++ ) {
                x_basic.data[i] = B.get(i,colB);
            }

            // Solve Q*x=b => x = Q'*b
            // Q_n*b = (I-gamma*u*u^T)*b = b - u*(gamma*U^T*b)
            for( int i = 0; i < rank; i++ ) {
                double u[] = qr[i];

                double vv = u[i];
                u[i] = 1;
                QrHelperFunctions_D64.rank1UpdateMultR(x_basic, u, gammas[i], 0, i, numRows, Y.data);
                u[i] = vv;
            }

            // solve for Rx = b using the standard upper triangular solver
            TriangularSolver.solveU(R11.data, x_basic.data, rank);

            // finish the basic solution by filling in zeros
            x_basic.reshape(numCols, 1, true);
            for( int i = rank; i < numCols; i++)
                x_basic.data[i] = 0;

            if( norm2Solution && rank < numCols )
                upgradeSolution(x_basic);

            // save the results
            for( int i = 0; i < numCols; i++ ) {
                X.set(pivots[i],colB,x_basic.data[i]);
            }
        }
    }

    @Override
    public boolean modifiesA() {
        return decomposition.inputModified();
    }

    @Override
    public boolean modifiesB() {
        return false;
    }
}

