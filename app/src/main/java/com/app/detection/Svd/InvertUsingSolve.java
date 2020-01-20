package com.app.detection.Svd;

public class InvertUsingSolve {

    public static void invert(LinearSolver<DenseMatrix64F> solver , RowD1Matrix64F A , DenseMatrix64F A_inv , DenseMatrix64F storage) {

        if( A.numRows != A_inv.numRows || A.numCols != A_inv.numCols) {
            throw new IllegalArgumentException("A and A_inv must have the same dimensions");
        }

        CommonOps.setIdentity(storage);

        solver.solve(storage,A_inv);
    }

    public static void invert(LinearSolver<DenseMatrix64F> solver , RowD1Matrix64F A , DenseMatrix64F A_inv ) {

        if( A.numRows != A_inv.numRows || A.numCols != A_inv.numCols) {
            throw new IllegalArgumentException("A and A_inv must have the same dimensions");
        }

        CommonOps.setIdentity(A_inv);

        solver.solve(A_inv,A_inv);
    }
}
