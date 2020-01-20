package com.app.detection.Svd;

public class LinearSolverLu_D64 extends LinearSolverLuBase_D64 {

    boolean doImprove = false;

    public LinearSolverLu_D64(LUDecompositionBase_D64 decomp) {
        super(decomp);
    }

    public LinearSolverLu_D64(LUDecompositionBase_D64 decomp, boolean doImprove) {
        super(decomp);
        this.doImprove = doImprove;
    }


    @Override
    public void solve(DenseMatrix64F b, DenseMatrix64F x) {
        if( b.numCols != x.numCols || b.numRows != numRows || x.numRows != numCols) {
            throw new IllegalArgumentException("Unexpected matrix size");
        }

        int numCols = b.numCols;

        double dataB[] = b.data;
        double dataX[] = x.data;

        double []vv = decomp._getVV();

//        for( int j = 0; j < numCols; j++ ) {
//            for( int i = 0; i < this.numCols; i++ ) vv[i] = dataB[i*numCols+j];
//            decomp._solveVectorInternal(vv);
//            for( int i = 0; i < this.numCols; i++ ) dataX[i*numCols+j] = vv[i];
//        }
        for( int j = 0; j < numCols; j++ ) {
            int index = j;
            for( int i = 0; i < this.numCols; i++ , index += numCols ) vv[i] = dataB[index];
            decomp._solveVectorInternal(vv);
            index = j;
            for( int i = 0; i < this.numCols; i++ , index += numCols ) dataX[index] = vv[i];
        }

        if( doImprove ) {
            improveSol(b,x);
        }
    }
}
