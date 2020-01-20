package com.app.detection.Svd;

public class LinearSolver_B64_to_D64 implements LinearSolver<DenseMatrix64F> {
    protected LinearSolver<BlockMatrix64F> alg = new BlockCholeskyOuterSolver();

    // block matrix copy of the system A matrix.
    protected BlockMatrix64F blockA = new BlockMatrix64F(1,1);
    // block matrix copy of B matrix passed into solve
    protected BlockMatrix64F blockB = new BlockMatrix64F(1,1);
    // block matrix copy of X matrix passed into solve
    protected BlockMatrix64F blockX = new BlockMatrix64F(1,1);

    public LinearSolver_B64_to_D64(LinearSolver<BlockMatrix64F> alg) {
        this.alg = alg;
    }

    /**
     * Converts 'A' into a block matrix and call setA() on the block matrix solver.
     *
     * @param A The A matrix in the linear equation. Not modified. Reference saved.
     * @return true if it can solve the system.
     */
    @Override
    public boolean setA(DenseMatrix64F A) {
        blockA.reshape(A.numRows,A.numCols,false);
        BlockMatrixOps.convert(A,blockA);

        return alg.setA(blockA);
    }

    @Override
    public double quality() {
        return alg.quality();
    }

    /**
     * Converts B and X into block matrices and calls the block matrix solve routine.
     *
     * @param B A matrix &real; <sup>m &times; p</sup>.  Not modified.
     * @param X A matrix &real; <sup>n &times; p</sup>, where the solution is written to.  Modified.
     */
    @Override
    public void solve(DenseMatrix64F B, DenseMatrix64F X) {
        blockB.reshape(B.numRows,B.numCols,false);
        blockX.reshape(X.numRows,X.numCols,false);
        BlockMatrixOps.convert(B,blockB);

        alg.solve(blockB,blockX);

        BlockMatrixOps.convert(blockX,X);
    }

    /**
     * Creates a block matrix the same size as A_inv, inverts the matrix and copies the results back
     * onto A_inv.
     *
     * @param A_inv Where the inverted matrix saved. Modified.
     */
    @Override
    public void invert(DenseMatrix64F A_inv) {
        blockB.reshape(A_inv.numRows,A_inv.numCols,false);

        alg.invert(blockB);

        BlockMatrixOps.convert(blockB,A_inv);
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
    public <D extends DecompositionInterface> D getDecomposition() {
        return alg.getDecomposition();
    }
}
