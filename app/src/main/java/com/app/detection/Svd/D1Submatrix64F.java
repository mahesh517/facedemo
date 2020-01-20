package com.app.detection.Svd;

public class D1Submatrix64F {
    public D1Matrix64F original;

    // bounding rows and columns
    public int row0,col0;
    public int row1,col1;

    public D1Submatrix64F() {
    }

    public D1Submatrix64F(D1Matrix64F original) {
        set(original);
    }

    public D1Submatrix64F(D1Matrix64F original,
                          int row0, int row1, int col0, int col1) {
        set(original,row0,row1,col0,col1);
    }

    public void set(D1Matrix64F original,
                    int row0, int row1, int col0, int col1) {
        this.original = original;
        this.row0 = row0;
        this.col0 = col0;
        this.row1 = row1;
        this.col1 = col1;
    }

    public void set(D1Matrix64F original) {
        this.original = original;
        row1 = original.numRows;
        col1 = original.numCols;
    }

    public int getRows() {
        return row1 - row0;
    }

    public int getCols() {
        return col1 - col0;
    }

    public double get(int row, int col ) {
        return original.get(row+row0,col+col0);
    }

    public void set(int row, int col, double value) {
        original.set(row+row0,col+col0,value);
    }

    public DenseMatrix64F extract() {
        DenseMatrix64F ret = new DenseMatrix64F(row1-row0,col1-col0);

        for( int i = 0; i < ret.numRows; i++ ) {
            for( int j = 0; j < ret.numCols; j++ ) {
                ret.set(i,j,get(i,j));
            }
        }

        return ret;
    }

    public void print() {
        MatrixIO.print(System.out,original,"%6.3f",row0,row1,col0,col1);
    }
}
