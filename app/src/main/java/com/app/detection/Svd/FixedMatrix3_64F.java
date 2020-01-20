package com.app.detection.Svd;

public class FixedMatrix3_64F implements FixedMatrix64F {
    public double a1,a2,a3;

    public FixedMatrix3_64F() {
    }

    public FixedMatrix3_64F(double a1,double a2,double a3)
    {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
    }

    public FixedMatrix3_64F(FixedMatrix3_64F o) {
        this.a1 = o.a1;
        this.a2 = o.a2;
        this.a3 = o.a3;
    }

    @Override
    public double get(int row, int col) {
        return unsafe_get(row,col);
    }

    @Override
    public double unsafe_get(int row, int col) {
        if( row != 0 && col != 0 )
            throw new IllegalArgumentException("Row or column must be zero since this is a vector");

        int w = Math.max(row,col);

        if( w == 0 ) {
            return a1;
        } else if( w == 1 ) {
            return a2;
        } else if( w == 2 ) {
            return a3;
        } else {
            throw new IllegalArgumentException("Out of range.  "+w);
        }
    }

    @Override
    public void set(int row, int col, double val) {
        unsafe_set(row,col,val);
    }

    @Override
    public void unsafe_set(int row, int col, double val) {
        if( row != 0 && col != 0 )
            throw new IllegalArgumentException("Row or column must be zero since this is a vector");

        int w = Math.max(row,col);

        if( w == 0 ) {
            a1 = val;
        } else if( w == 1 ) {
            a2 = val;
        } else if( w == 2 ) {
            a3 = val;
        } else {
            throw new IllegalArgumentException("Out of range.  "+w);
        }
    }

    @Override
    public void set(Matrix original) {
        RealMatrix64F m = (RealMatrix64F)original;

        if( m.getNumCols() == 1 && m.getNumRows() == 3 ) {
            a1 = m.get(0,0);
            a2 = m.get(1,0);
            a3 = m.get(2,0);
        } else if( m.getNumRows() == 1 && m.getNumCols() == 3 ){
            a1 = m.get(0,0);
            a2 = m.get(0,1);
            a3 = m.get(0,2);
        } else {
            throw new IllegalArgumentException("Incompatible shape");
        }
    }

    @Override
    public int getNumRows() {
        return 3;
    }

    @Override
    public int getNumCols() {
        return 1;
    }

    @Override
    public int getNumElements() {
        return 3;
    }

    @Override
    public <T extends Matrix> T copy() {
        return (T)new FixedMatrix3_64F(this);
    }

    @Override
    public void print() {
        MatrixIO.print(System.out, this);
    }
}

