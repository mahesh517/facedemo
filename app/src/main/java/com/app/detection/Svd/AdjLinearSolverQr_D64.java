package com.app.detection.Svd;

public class AdjLinearSolverQr_D64 extends LinearSolverQr_D64 implements AdjustableLinearSolver {

    private QrUpdate update;

    private DenseMatrix64F A;

    public AdjLinearSolverQr_D64() {
        super( new QRDecompositionHouseholderColumn_D64() );
    }

    @Override
    public void setMaxSize( int maxRows , int maxCols ) {
        // allow it some room to grow
        maxRows += 5;

        super.setMaxSize(maxRows,maxCols);

        update = new QrUpdate(maxRows,maxCols,true);
        A = new DenseMatrix64F(maxRows,maxCols);
    }

    /**
     * Compute the A matrix from the Q and R matrices.
     *
     * @return The A matrix.
     */
    @Override
    public DenseMatrix64F getA() {
        if( A.data.length < numRows*numCols ) {
            A = new DenseMatrix64F(numRows,numCols);
        }
        A.reshape(numRows,numCols, false);
        CommonOps.mult(Q,R,A);

        return A;
    }

    @Override
    public boolean addRowToA(double[] A_row , int rowIndex ) {
        // see if it needs to grow the data structures
        if( numRows + 1 > maxRows) {
            // grow by 10%
            int grow = maxRows / 10;
            if( grow < 1 ) grow = 1;
            maxRows = numRows + grow;
            Q.reshape(maxRows,maxRows,true);
            R.reshape(maxRows,maxCols,true);
        }

        update.addRow(Q,R,A_row,rowIndex,true);
        numRows++;

        return true;
    }

    @Override
    public boolean removeRowFromA(int index) {
        update.deleteRow(Q,R,index,true);
        numRows--;
        return true;
    }

}
