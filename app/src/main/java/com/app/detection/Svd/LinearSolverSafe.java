package com.app.detection.Svd;

public class LinearSolverSafe<T extends ReshapeMatrix> implements LinearSolver<T> {

    // the solver it is wrapped around
    private LinearSolver<T> alg;

    // local copies of input matrices that can be modified.
    private T A;
    private T B;

    /**
     *
     * @param alg The solver it is wrapped around.
     */
    public LinearSolverSafe(LinearSolver<T> alg) {
        this.alg = alg;
    }

    @Override
    public boolean setA(T A) {

        if( alg.modifiesA() ) {
            if( this.A == null ) {
                this.A = (T)A.copy();
            } else {
                if( this.A.getNumRows() != A.getNumRows() || this.A.getNumCols() != A.getNumCols() ) {
                    this.A.reshape(A.getNumRows(),A.getNumCols());
                }
                this.A.set(A);
            }
            return alg.setA(this.A);
        }

        return alg.setA(A);
    }

    @Override
    public double quality() {
        return alg.quality();
    }

    @Override
    public void solve(T B, T X) {
        if( alg.modifiesB() ) {
            if( this.B == null ) {
                this.B = (T)B.copy();
            } else {
                if( this.B.getNumRows() != B.getNumRows() || this.B.getNumCols() != B.getNumCols() ) {
                    this.B.reshape(A.getNumRows(),B.getNumCols());
                }
                this.B.set(B);
            }
            B = this.B;
        }

        alg.solve(B,X);
    }

    @Override
    public void invert(T A_inv) {
        alg.invert(A_inv);
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
