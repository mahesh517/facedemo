package com.app.detection.Svd;

public class LinearSolverChol_B64 extends LinearSolver_B64_to_D64 {

    public LinearSolverChol_B64() {
        super(new BlockCholeskyOuterSolver());
    }

    /**
     * Only converts the B matrix and passes that onto solve.  Te result is then copied into
     * the input 'X' matrix.
     *
     * @param B A matrix &real; <sup>m &times; p</sup>.  Not modified.
     * @param X A matrix &real; <sup>n &times; p</sup>, where the solution is written to.  Modified.
     */
    @Override
    public void solve(DenseMatrix64F B, DenseMatrix64F X) {
        blockB.reshape(B.numRows,B.numCols,false);
        BlockMatrixOps.convert(B,blockB);

        // since overwrite B is true X does not need to be passed in
        alg.solve(blockB,null);

        BlockMatrixOps.convert(blockB,X);
    }

}
