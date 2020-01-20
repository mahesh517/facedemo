package com.app.detection.Svd;

public abstract class LinearSolverAbstract_D64 implements LinearSolver<DenseMatrix64F> {

    protected DenseMatrix64F A;
    protected int numRows;
    protected int numCols;

    public DenseMatrix64F getA() {
        return A;
    }

    protected void _setA(DenseMatrix64F A) {
        this.A = A;
        this.numRows = A.numRows;
        this.numCols = A.numCols;
    }

    @Override
    public void invert(DenseMatrix64F A_inv) {
        InvertUsingSolve.invert(this,A,A_inv);
    }
}

